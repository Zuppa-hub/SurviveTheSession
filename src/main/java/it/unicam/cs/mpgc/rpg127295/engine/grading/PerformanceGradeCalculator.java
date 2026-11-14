package it.unicam.cs.mpgc.rpg127295.engine.grading;

import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.Objects;
import java.util.Optional;

/**
 * Grades a performance by the share of correct answers.
 * <ul>
 *   <li>Sufficient from 60% of correct answers; below, or with no answers, the exam is failed.</li>
 *   <li>From 60% to 100% the grade grows linearly from 18 to 30, rounded down.</li>
 *   <li>Each hint used costs one point, but never below 18.</li>
 *   <li>Honors only for a flawless performance: all answers correct and no hints.</li>
 * </ul>
 * The computation uses integers only, so that a share exactly on the threshold (such as 3
 * out of 5) is not affected by floating point rounding.
 */
public final class PerformanceGradeCalculator implements GradeCalculator {

    private static final int THRESHOLD_NUMERATOR = 3;
    private static final int THRESHOLD_DENOMINATOR = 5;
    private static final int MIN_GRADE = 18;
    private static final int MAX_GRADE = 30;
    private static final int HONORS = 31;
    private static final int HINT_PENALTY = 1;

    @Override
    public Optional<Grade> calculate(Performance performance) {
        Objects.requireNonNull(performance, "Performance must not be null");
        if (!isSufficient(performance)) {
            return Optional.empty();
        }
        if (performance.isFlawless()) {
            return Optional.of(Grade.of(HONORS));
        }
        return Optional.of(Grade.of(withHintPenalty(baseGrade(performance), performance.hintsUsed())));
    }

    /** correct / answered >= 3 / 5, compared without division. */
    private static boolean isSufficient(Performance performance) {
        return performance.answered() > 0
                && performance.correctAnswers() * THRESHOLD_DENOMINATOR
                >= performance.answered() * THRESHOLD_NUMERATOR;
    }

    /**
     * Maps the share of correct answers from [3/5, 1] to [18, 30], rounding down:
     * 18 + 12 * (correct/answered - 3/5) / (2/5).
     */
    private static int baseGrade(Performance performance) {
        int answered = performance.answered();
        int aboveThreshold = performance.correctAnswers() * THRESHOLD_DENOMINATOR - answered * THRESHOLD_NUMERATOR;
        int range = MAX_GRADE - MIN_GRADE;
        int thresholdGap = THRESHOLD_DENOMINATOR - THRESHOLD_NUMERATOR;
        return MIN_GRADE + range * aboveThreshold / (thresholdGap * answered);
    }

    private static int withHintPenalty(int grade, int hintsUsed) {
        return Math.max(MIN_GRADE, grade - hintsUsed * HINT_PENALTY);
    }
}
