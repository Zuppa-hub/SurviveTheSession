package it.unicam.cs.mpgc.rpg127295.model.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ExamTest {

    private static final Subject PROGRAMMING = new Subject("PROG", "Programmazione");
    private static final Subject ALGORITHMS = new Subject("ASD", "Algoritmi e Strutture Dati");

    private static Exam programmingExam() {
        return new Exam("PROG1", PROGRAMMING, ExamType.WRITTEN, 12, Difficulty.MEDIUM);
    }

    @Test
    void exposesItsData() {
        Exam exam = programmingExam();

        assertEquals("PROG1", exam.code());
        assertEquals(PROGRAMMING, exam.subject());
        assertEquals(ExamType.WRITTEN, exam.type());
        assertEquals(12, exam.credits());
        assertEquals(Difficulty.MEDIUM, exam.difficulty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankCode(String code) {
        assertThrows(IllegalArgumentException.class,
                () -> new Exam(code, PROGRAMMING, ExamType.WRITTEN, 12, Difficulty.MEDIUM));
    }

    @Test
    void rejectsMissingSubject() {
        assertThrows(NullPointerException.class,
                () -> new Exam("PROG1", null, ExamType.WRITTEN, 12, Difficulty.MEDIUM));
    }

    @Test
    void rejectsMissingType() {
        assertThrows(NullPointerException.class,
                () -> new Exam("PROG1", PROGRAMMING, null, 12, Difficulty.MEDIUM));
    }

    @Test
    void rejectsMissingDifficulty() {
        assertThrows(NullPointerException.class,
                () -> new Exam("PROG1", PROGRAMMING, ExamType.WRITTEN, 12, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -6, 0})
    void rejectsNonPositiveCredits(int credits) {
        assertThrows(IllegalArgumentException.class,
                () -> new Exam("PROG1", PROGRAMMING, ExamType.WRITTEN, credits, Difficulty.MEDIUM));
    }

    @Test
    void examsWithSameCodeAreEqualEvenIfOtherDataDiffer() {
        Exam other = new Exam("PROG1", ALGORITHMS, ExamType.ORAL, 6, Difficulty.HARD);

        assertEquals(programmingExam(), other);
        assertEquals(programmingExam().hashCode(), other.hashCode());
    }

    @Test
    void examsWithDifferentCodeAreNotEqual() {
        Exam other = new Exam("PROG2", PROGRAMMING, ExamType.WRITTEN, 12, Difficulty.MEDIUM);

        assertNotEquals(programmingExam(), other);
    }

    @Test
    void worksAsMapKey() {
        Map<Exam, Grade> transcript = Map.of(programmingExam(), Grade.of(28));

        Exam sameExam = new Exam("PROG1", PROGRAMMING, ExamType.WRITTEN, 12, Difficulty.MEDIUM);

        assertEquals(Grade.of(28), transcript.get(sameExam));
    }

    @Test
    void toStringShowsCodeAndSubjectName() {
        assertEquals("PROG1 - Programmazione", programmingExam().toString());
    }
}
