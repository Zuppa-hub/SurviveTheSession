package it.unicam.cs.mpgc.rpg127295.engine.game;

import it.unicam.cs.mpgc.rpg127295.model.exam.ExamSession;
import it.unicam.cs.mpgc.rpg127295.model.transcript.Transcript;
import java.util.Objects;

/**
 * The graduation rules, kept out of the transcript (SRP): the transcript records grades and
 * computes the average; when the student graduates, and with which grade, is a game rule that
 * can change on its own.
 * <p>
 * The final grade is the weighted average on a scale of 110: average x 110 / 30, rounded, so
 * an average of 30 gives 110 and one of 18 gives 66. Bonus points and honors at graduation
 * depend on the rules of each degree course and are left out.
 */
public final class Graduation {

    private static final int GRADE_SCALE = 30;
    private static final int GRADUATION_SCALE = 110;

    /** @return true if every exam of the session is in the transcript */
    public boolean canGraduate(ExamSession session, Transcript transcript) {
        Objects.requireNonNull(session, "Session must not be null");
        Objects.requireNonNull(transcript, "Transcript must not be null");
        return session.exams().stream().allMatch(transcript::hasPassed);
    }

    /**
     * @return the final grade, from 66 to 110
     * @throws IllegalStateException if the transcript has no exams
     */
    public int finalGrade(Transcript transcript) {
        Objects.requireNonNull(transcript, "Transcript must not be null");
        double average = transcript.weightedAverage()
                .orElseThrow(() -> new IllegalStateException("No exam passed, no final grade"));
        return (int) Math.round(average * GRADUATION_SCALE / GRADE_SCALE);
    }
}
