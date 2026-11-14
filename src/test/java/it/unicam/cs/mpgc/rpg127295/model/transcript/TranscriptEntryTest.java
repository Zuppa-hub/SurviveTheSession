package it.unicam.cs.mpgc.rpg127295.model.transcript;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import org.junit.jupiter.api.Test;

class TranscriptEntryTest {

    private static final Exam PROGRAMMING = new Exam(
            "PROG1", new Subject("PROG", "Programmazione"), ExamType.WRITTEN, 12, Difficulty.MEDIUM);

    @Test
    void exposesExamAndGrade() {
        TranscriptEntry entry = new TranscriptEntry(PROGRAMMING, Grade.of(28));

        assertAll(
                () -> assertEquals(PROGRAMMING, entry.exam()),
                () -> assertEquals(Grade.of(28), entry.grade()));
    }

    @Test
    void rejectsMissingReferences() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new TranscriptEntry(null, Grade.of(28))),
                () -> assertThrows(NullPointerException.class, () -> new TranscriptEntry(PROGRAMMING, null)));
    }
}
