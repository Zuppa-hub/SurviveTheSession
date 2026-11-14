package it.unicam.cs.mpgc.rpg127295.model.transcript;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.Objects;

/**
 * A line of the transcript: a passed exam with its grade. Immutable value object.
 *
 * @param exam  the passed exam
 * @param grade the grade obtained
 */
public record TranscriptEntry(Exam exam, Grade grade) {

    /** @throws NullPointerException if exam or grade is null */
    public TranscriptEntry {
        Objects.requireNonNull(exam, "Exam must not be null");
        Objects.requireNonNull(grade, "Grade must not be null");
    }
}
