package it.unicam.cs.mpgc.rpg127295.model.exam;

import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.Objects;

/**
 * An exam of the session, as defined in the content files.
 * <p>
 * Immutable. Its identity is the {@code code}: two exams with the same code are the same exam,
 * which makes {@code Exam} usable as a key in the transcript. Balancing values such as the
 * exam resistance are not stored here: the engine derives them from credits and difficulty.
 */
public final class Exam {

    private final String code;
    private final Subject subject;
    private final ExamType type;
    private final int credits;
    private final Difficulty difficulty;

    /**
     * @param code       unique code, such as "PROG1"; never blank
     * @param subject    the subject of the exam
     * @param type       written, oral or project
     * @param credits    CFU awarded when passed; always positive
     * @param difficulty difficulty of the exam
     * @throws IllegalArgumentException if code is blank or credits are not positive
     * @throws NullPointerException     if subject, type or difficulty is null
     */
    public Exam(String code, Subject subject, ExamType type, int credits, Difficulty difficulty) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Exam code must not be blank");
        }
        if (credits <= 0) {
            throw new IllegalArgumentException("Exam credits must be positive: " + credits);
        }
        this.code = code;
        this.subject = Objects.requireNonNull(subject, "Exam subject must not be null");
        this.type = Objects.requireNonNull(type, "Exam type must not be null");
        this.credits = credits;
        this.difficulty = Objects.requireNonNull(difficulty, "Exam difficulty must not be null");
    }

    public String code() {
        return code;
    }

    public Subject subject() {
        return subject;
    }

    public ExamType type() {
        return type;
    }

    public int credits() {
        return credits;
    }

    public Difficulty difficulty() {
        return difficulty;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Exam exam && code.equals(exam.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code + " - " + subject.name();
    }
}
