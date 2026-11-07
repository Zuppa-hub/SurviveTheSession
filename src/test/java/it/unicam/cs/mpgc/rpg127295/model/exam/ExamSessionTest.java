package it.unicam.cs.mpgc.rpg127295.model.exam;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExamSessionTest {

    private static final Subject SUBJECT = new Subject("PROG", "Programmazione");
    private static final Exam FIRST = exam("PROG1");
    private static final Exam SECOND = exam("ASD");
    private static final Exam BOSS = exam("MDP");

    private static Exam exam(String code) {
        return new Exam(code, SUBJECT, ExamType.WRITTEN, 6, Difficulty.MEDIUM);
    }

    @Test
    void keepsTheExamsInOrder() {
        assertEquals(List.of(FIRST, SECOND, BOSS), new ExamSession(List.of(FIRST, SECOND, BOSS)).exams());
    }

    @Test
    void theLastExamIsTheBoss() {
        assertEquals(BOSS, new ExamSession(List.of(FIRST, SECOND, BOSS)).boss());
    }

    @Test
    void knowsItsExams() {
        ExamSession session = new ExamSession(List.of(FIRST, SECOND));

        assertAll(
                () -> assertTrue(session.contains(SECOND)),
                () -> assertFalse(session.contains(BOSS)));
    }

    @Test
    void isNotAffectedByChangesToTheOriginalList() {
        List<Exam> exams = new ArrayList<>(List.of(FIRST));
        ExamSession session = new ExamSession(exams);

        exams.add(SECOND);

        assertAll(
                () -> assertEquals(List.of(FIRST), session.exams()),
                () -> assertThrows(UnsupportedOperationException.class, () -> session.exams().add(BOSS)));
    }

    @Test
    void rejectsAnEmptySessionOrTheSameExamTwice() {
        List<Exam> sameCode = List.of(FIRST, exam("PROG1"));

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new ExamSession(List.of())),
                () -> assertThrows(IllegalArgumentException.class, () -> new ExamSession(sameCode)),
                () -> assertThrows(NullPointerException.class, () -> new ExamSession(null)));
    }
}
