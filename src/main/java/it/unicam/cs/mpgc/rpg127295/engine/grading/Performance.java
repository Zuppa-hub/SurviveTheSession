package it.unicam.cs.mpgc.rpg127295.engine.grading;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonNegative;

/**
 * How the student performed in an exam, the input of a {@link GradeCalculator}.
 * Immutable value object.
 * <p>
 * Energy left and time taken are not part of it on purpose: energy already decides whether
 * the student fails, and time exists only in written exams, so grades stay comparable across
 * exam types.
 *
 * @param correctAnswers answers given correctly
 * @param wrongAnswers   answers given wrongly, timeouts included
 * @param hintsUsed      hints shown to the student, the only penalty
 */
public record Performance(int correctAnswers, int wrongAnswers, int hintsUsed) {

    /** @throws IllegalArgumentException if a count is negative */
    public Performance {
        requireNonNegative(correctAnswers, "Correct answers");
        requireNonNegative(wrongAnswers, "Wrong answers");
        requireNonNegative(hintsUsed, "Hints used");
    }

    /** @return the number of answers given, correct or wrong */
    public int answered() {
        return correctAnswers + wrongAnswers;
    }

    /** @return true if at least one answer was given, with no mistakes and no hints */
    public boolean isFlawless() {
        return correctAnswers > 0 && wrongAnswers == 0 && hintsUsed == 0;
    }
}
