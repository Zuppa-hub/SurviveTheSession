package it.unicam.cs.mpgc.rpg127295.engine.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamSession;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.transcript.Transcript;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GraduationTest {

    private static final Exam TWELVE = exam("PROG1", 12);
    private static final Exam SIX = exam("ASD", 6);
    private static final ExamSession SESSION = new ExamSession(List.of(TWELVE, SIX));

    private final Graduation graduation = new Graduation();

    private static Exam exam(String code, int credits) {
        return new Exam(code, TestQuestions.SUBJECT, ExamType.WRITTEN, credits, Difficulty.MEDIUM);
    }

    private static Transcript transcript(int twelveCreditsGrade, int sixCreditsGrade) {
        Transcript transcript = new Transcript();
        transcript.record(TWELVE, Grade.of(twelveCreditsGrade));
        transcript.record(SIX, Grade.of(sixCreditsGrade));
        return transcript;
    }

    @Test
    void canGraduateOnlyWithEveryExamOfTheSessionPassed() {
        Transcript halfway = new Transcript();
        halfway.record(TWELVE, Grade.of(25));

        assertAll(
                () -> assertFalse(graduation.canGraduate(SESSION, new Transcript())),
                () -> assertFalse(graduation.canGraduate(SESSION, halfway)),
                () -> assertTrue(graduation.canGraduate(SESSION, transcript(25, 25))));
    }

    @ParameterizedTest(name = "{0} (12 CFU) and {1} (6 CFU) -> {2}")
    @CsvSource({
        "30, 30, 110",
        "31, 31, 110",
        "18, 18,  66",
        "30, 24, 103",
        "27, 27,  99"
    })
    void theFinalGradeIsTheWeightedAverageOnAHundredAndTenRounded(int twelve, int six, int expected) {
        assertEquals(expected, graduation.finalGrade(transcript(twelve, six)));
    }

    @Test
    void thereIsNoFinalGradeWithoutExams() {
        assertThrows(IllegalStateException.class, () -> graduation.finalGrade(new Transcript()));
    }
}
