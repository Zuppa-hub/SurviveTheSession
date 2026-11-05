package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.model.question.ChoiceQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.FillInQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.random.RandomGenerator;

/**
 * A question as shown to the player: the question itself, its options in display order and
 * whether its hint is visible. The question is never modified: order, removed options and
 * hint belong to this presentation and are forgotten with the next question. Since an answer
 * is the text of an option, none of this affects {@link Question#isCorrect(String)}.
 *
 * @param question    the question
 * @param options     the options shown, in display order: a subset of those of a choice
 *                    question that still contains the correct one; empty for a fill-in
 *                    question
 * @param hintVisible whether the hint is shown; only for a question with a hint
 */
public record PresentedQuestion(Question question, List<String> options, boolean hintVisible) {

    private static final QuestionVisitor<List<String>> OPTIONS_OF = new QuestionVisitor<>() {
        @Override
        public List<String> visitChoice(ChoiceQuestion question) {
            return question.options();
        }

        @Override
        public List<String> visitFillIn(FillInQuestion question) {
            return List.of();
        }
    };

    /**
     * @throws NullPointerException     if an argument is null
     * @throws IllegalArgumentException if the options are not a valid subset of those of the
     *                                  question, or the hint is visible but missing
     */
    public PresentedQuestion {
        Objects.requireNonNull(question, "Question must not be null");
        options = List.copyOf(Objects.requireNonNull(options, "Options must not be null"));
        requireValidOptions(question, options);
        if (hintVisible && question.hint().isEmpty()) {
            throw new IllegalArgumentException("Question " + question.id() + " has no hint to show");
        }
    }

    /**
     * Presents a question with its options shuffled by the given generator and the hint hidden.
     */
    public static PresentedQuestion of(Question question, RandomGenerator random) {
        Objects.requireNonNull(question, "Question must not be null");
        Objects.requireNonNull(random, "Random generator must not be null");
        List<String> options = new ArrayList<>(question.accept(OPTIONS_OF));
        Collections.shuffle(options, random);
        return new PresentedQuestion(question, options, false);
    }

    /** @return the options shown that are wrong; empty for a fill-in question */
    public List<String> wrongOptions() {
        return options.stream()
                .filter(option -> !question.isCorrect(option))
                .toList();
    }

    /**
     * @return a presentation without {@code count} wrong options, chosen by the generator
     * @throws IllegalArgumentException if fewer wrong options are shown
     */
    public PresentedQuestion withoutWrongOptions(int count, RandomGenerator random) {
        Objects.requireNonNull(random, "Random generator must not be null");
        List<String> wrong = new ArrayList<>(wrongOptions());
        if (count > wrong.size()) {
            throw new IllegalArgumentException(
                    "Cannot remove " + count + " wrong options, only " + wrong.size() + " are shown");
        }
        Set<String> removed = new HashSet<>();
        for (int i = 0; i < count; i++) {
            removed.add(wrong.remove(random.nextInt(wrong.size())));
        }
        List<String> kept = options.stream()
                .filter(option -> !removed.contains(option))
                .toList();
        return new PresentedQuestion(question, kept, hintVisible);
    }

    /**
     * @return the same presentation with the hint visible
     * @throws IllegalStateException if the hint is already visible
     */
    public PresentedQuestion withHintShown() {
        if (hintVisible) {
            throw new IllegalStateException("The hint of " + question.id() + " is already shown");
        }
        return new PresentedQuestion(question, options, true);
    }

    private static void requireValidOptions(Question question, List<String> options) {
        List<String> original = question.accept(OPTIONS_OF);
        boolean subset = new HashSet<>(original).containsAll(options);
        boolean distinct = new HashSet<>(options).size() == options.size();
        boolean keepsCorrect = original.isEmpty() || options.stream().anyMatch(question::isCorrect);
        if (!subset || !distinct || !keepsCorrect) {
            throw new IllegalArgumentException("Options do not match question " + question.id() + ": " + options);
        }
    }
}
