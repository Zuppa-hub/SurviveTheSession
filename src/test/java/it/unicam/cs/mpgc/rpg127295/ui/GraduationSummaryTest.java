package it.unicam.cs.mpgc.rpg127295.ui;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CORRECT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleListener;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.List;
import org.junit.jupiter.api.Test;

class GraduationSummaryTest {

    private static final Exam EXAM = new Exam("PROG1", TestQuestions.SUBJECT, ExamType.ORAL, 12, Difficulty.EASY);

    private static Game graduatedGame() {
        Game game = TestGames.game(EXAM);
        game.startExam(EXAM, new BattleListener() {
        }).answer(CORRECT);
        return game;
    }

    @Test
    void summarisesTheTranscriptAndTheFinalGrade() {
        GraduationSummary summary = GraduationSummary.of(graduatedGame());

        assertAll(
                () -> assertEquals("Andrea", summary.studentName()),
                () -> assertEquals(List.of("Programmazione · 12 CFU · 28"), summary.exams()),
                () -> assertEquals("28,00", summary.averageLabel()),
                () -> assertEquals(12, summary.credits()),
                () -> assertEquals(103, summary.finalGrade()));
    }

    @Test
    void existsOnlyWhenTheStudentCanGraduate() {
        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> GraduationSummary.of(TestGames.game(EXAM))),
                () -> assertThrows(NullPointerException.class, () -> GraduationSummary.of(null)));
    }
}
