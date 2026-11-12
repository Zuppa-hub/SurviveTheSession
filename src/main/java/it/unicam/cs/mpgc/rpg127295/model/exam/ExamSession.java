package it.unicam.cs.mpgc.rpg127295.model.exam;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * The exams of the session, in order. The last one is the final boss.
 * <p>
 * The session does not know which exams are passed: that is recorded once, in the transcript,
 * and the game combines the two. Keeping a second list here would duplicate that state.
 */
public final class ExamSession {

    private final List<Exam> exams;

    /**
     * @param exams at least one exam, no two with the same code; the list is copied
     * @throws IllegalArgumentException if the list is empty or contains an exam twice
     */
    public ExamSession(List<Exam> exams) {
        this.exams = List.copyOf(Objects.requireNonNull(exams, "Exams must not be null"));
        if (this.exams.isEmpty()) {
            throw new IllegalArgumentException("A session needs at least one exam");
        }
        if (new HashSet<>(this.exams).size() != this.exams.size()) {
            throw new IllegalArgumentException("A session cannot contain the same exam twice: " + this.exams);
        }
    }

    /** @return the exams in order; unmodifiable */
    public List<Exam> exams() {
        return exams;
    }

    /** @return the last exam, available only when all the others are passed */
    public Exam boss() {
        return exams.getLast();
    }

    public boolean contains(Exam exam) {
        return exams.contains(exam);
    }
}
