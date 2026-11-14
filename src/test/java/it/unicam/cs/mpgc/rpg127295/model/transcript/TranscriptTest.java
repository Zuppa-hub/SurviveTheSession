package it.unicam.cs.mpgc.rpg127295.model.transcript;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TranscriptTest {

    private static final Exam PROGRAMMING = exam("PROG1", "Programmazione", 12);
    private static final Exam ALGORITHMS = exam("ASD", "Algoritmi e Strutture Dati", 6);
    private static final Exam DATABASES = exam("BD", "Basi di Dati", 9);

    private Transcript transcript;

    private static Exam exam(String code, String subjectName, int credits) {
        return new Exam(code, new Subject(code, subjectName), ExamType.WRITTEN, credits, Difficulty.MEDIUM);
    }

    @BeforeEach
    void createEmptyTranscript() {
        transcript = new Transcript();
    }

    @Test
    void startsEmpty() {
        assertAll(
                () -> assertEquals(List.of(), transcript.entries()),
                () -> assertEquals(0, transcript.totalCredits()),
                () -> assertEquals(OptionalDouble.empty(), transcript.weightedAverage()),
                () -> assertFalse(transcript.hasPassed(PROGRAMMING)),
                () -> assertEquals(Optional.empty(), transcript.gradeOf(PROGRAMMING)));
    }

    @Test
    void recordsAPassedExam() {
        transcript.record(PROGRAMMING, Grade.of(28));

        assertAll(
                () -> assertTrue(transcript.hasPassed(PROGRAMMING)),
                () -> assertEquals(Optional.of(Grade.of(28)), transcript.gradeOf(PROGRAMMING)),
                () -> assertFalse(transcript.hasPassed(ALGORITHMS)));
    }

    @Test
    void rejectsTheSameExamTwice() {
        transcript.record(PROGRAMMING, Grade.of(28));

        assertThrows(IllegalStateException.class, () -> transcript.record(PROGRAMMING, Grade.of(30)));
    }

    @Test
    void recognisesTheSameExamByCode() {
        transcript.record(PROGRAMMING, Grade.of(28));
        Exam sameCode = exam("PROG1", "Programmazione", 12);

        assertAll(
                () -> assertTrue(transcript.hasPassed(sameCode)),
                () -> assertThrows(IllegalStateException.class, () -> transcript.record(sameCode, Grade.of(25))));
    }

    @Test
    void keepsEntriesInRecordingOrder() {
        transcript.record(DATABASES, Grade.of(24));
        transcript.record(PROGRAMMING, Grade.of(28));
        transcript.record(ALGORITHMS, Grade.of(31));

        assertEquals(List.of(
                new TranscriptEntry(DATABASES, Grade.of(24)),
                new TranscriptEntry(PROGRAMMING, Grade.of(28)),
                new TranscriptEntry(ALGORITHMS, Grade.of(31))), transcript.entries());
    }

    @Test
    void entriesIsAnUnmodifiableSnapshot() {
        transcript.record(PROGRAMMING, Grade.of(28));
        List<TranscriptEntry> snapshot = transcript.entries();

        transcript.record(ALGORITHMS, Grade.of(30));

        assertAll(
                () -> assertEquals(1, snapshot.size()),
                () -> assertThrows(UnsupportedOperationException.class, () -> snapshot.clear()));
    }

    @Test
    void sumsTheCreditsOfPassedExams() {
        transcript.record(PROGRAMMING, Grade.of(28));
        transcript.record(ALGORITHMS, Grade.of(18));

        assertEquals(18, transcript.totalCredits());
    }

    @Test
    void weightsTheAverageByCreditsWithHonorsCountingThirty() {
        transcript.record(PROGRAMMING, Grade.of(27));
        transcript.record(ALGORITHMS, Grade.of(31));

        double expected = (12 * 27 + 6 * 30) / 18.0;

        assertEquals(expected, transcript.weightedAverage().orElseThrow(), 1e-9);
    }

    @Test
    void averageOfASingleExamIsItsGrade() {
        transcript.record(DATABASES, Grade.of(25));

        assertEquals(25.0, transcript.weightedAverage().orElseThrow(), 1e-9);
    }

    @Test
    void rejectsMissingReferences() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> transcript.record(null, Grade.of(28))),
                () -> assertThrows(NullPointerException.class, () -> transcript.record(PROGRAMMING, null)));
    }
}
