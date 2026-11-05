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
import java.util.random.RandomGenerator;

/**
 * A question as shown to the player: the question itself and its options in display order.
 * The question is never modified; the order is a separate list. Since an answer is the text
 * of an option, the order does not affect {@link Question#isCorrect(String)}.
 *
 * @param question the question
 * @param options  the options of a choice question in display order; empty for a fill-in
 *                 question
 */
public record PresentedQuestion(Question question, List<String> options) {

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
     * @throws IllegalArgumentException if the options are not exactly those of the question
     */
    public PresentedQuestion {
        Objects.requireNonNull(question, "Question must not be null");
        options = List.copyOf(Objects.requireNonNull(options, "Options must not be null"));
        if (!sameOptions(options, question.accept(OPTIONS_OF))) {
            throw new IllegalArgumentException("Options do not match question " + question.id() + ": " + options);
        }
    }

    /**
     * Presents a question with its options shuffled by the given generator.
     */
    public static PresentedQuestion of(Question question, RandomGenerator random) {
        Objects.requireNonNull(question, "Question must not be null");
        Objects.requireNonNull(random, "Random generator must not be null");
        List<String> options = new ArrayList<>(question.accept(OPTIONS_OF));
        Collections.shuffle(options, random);
        return new PresentedQuestion(question, options);
    }

    private static boolean sameOptions(List<String> shown, List<String> original) {
        return shown.size() == original.size() && new HashSet<>(shown).equals(new HashSet<>(original));
    }
}
