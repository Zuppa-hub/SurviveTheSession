package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.random.RandomGenerator;

/**
 * The questions available in one battle. A question drawn is never drawn again: the questions
 * already asked are kept in a {@link HashSet}, so two questions with the same id count as the
 * same question. The choice among the candidates is made by the injected generator.
 */
public final class QuestionPool {

    private final List<Question> questions;
    private final Set<Question> asked = new HashSet<>();
    private final RandomGenerator random;

    /**
     * @param questions the questions of the subject; the list is copied
     * @param random    the generator used to choose among the candidates
     */
    public QuestionPool(List<Question> questions, RandomGenerator random) {
        this.questions = List.copyOf(Objects.requireNonNull(questions, "Questions must not be null"));
        this.random = Objects.requireNonNull(random, "Random generator must not be null");
    }

    /** @return a question not asked yet, or empty if none is left */
    public Optional<Question> drawAny() {
        return draw(available());
    }

    /**
     * Draws a question of the requested difficulty or, if none is left, of the closest one;
     * between two equally close difficulties the harder wins.
     *
     * @return a question not asked yet, or empty if none is left
     */
    public Optional<Question> drawClosestTo(Difficulty difficulty) {
        Objects.requireNonNull(difficulty, "Difficulty must not be null");
        return draw(closestTo(difficulty));
    }

    public boolean isEmpty() {
        return remaining() == 0;
    }

    /** @return the number of questions not asked yet */
    public int remaining() {
        return available().size();
    }

    private List<Question> available() {
        return questions.stream()
                .filter(question -> !asked.contains(question))
                .toList();
    }

    private List<Question> closestTo(Difficulty target) {
        List<Question> available = available();
        return available.stream()
                .map(Question::difficulty)
                .min(byDistanceFrom(target))
                .map(closest -> withDifficulty(available, closest))
                .orElse(List.of());
    }

    private static Comparator<Difficulty> byDistanceFrom(Difficulty target) {
        Comparator<Difficulty> byDistance =
                Comparator.comparingInt(difficulty -> Math.abs(difficulty.ordinal() - target.ordinal()));
        return byDistance.thenComparing(Comparator.reverseOrder());
    }

    private static List<Question> withDifficulty(List<Question> questions, Difficulty difficulty) {
        return questions.stream()
                .filter(question -> question.difficulty() == difficulty)
                .toList();
    }

    private Optional<Question> draw(List<Question> candidates) {
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        Question question = candidates.get(random.nextInt(candidates.size()));
        asked.add(question);
        return Optional.of(question);
    }
}
