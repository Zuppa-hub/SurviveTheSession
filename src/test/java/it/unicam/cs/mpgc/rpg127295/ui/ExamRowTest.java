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
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ExamRowTest {

    private static final Exam FIRST = new Exam("PROG1", TestQuestions.SUBJECT, ExamType.ORAL, 1, Difficulty.EASY);
    private static final Exam BOSS = new Exam("MDP1", TestQuestions.SUBJECT, ExamType.PROJECT, 1, Difficulty.EASY);

    private static Game game() {
        return TestGames.game(FIRST, BOSS);
    }

    @Test
    void atTheStartTheBossIsLocked() {
        assertEquals(List.of(
                        new ExamRow(FIRST, ExamRow.Status.AVAILABLE, Optional.empty()),
                        new ExamRow(BOSS, ExamRow.Status.LOCKED, Optional.empty())),
                ExamRow.of(game()));
    }

    @Test
    void aPassedExamShowsItsGradeAndUnlocksTheBoss() {
        Game game = game();
        game.startExam(FIRST, new BattleListener() {
        }).answer(CORRECT);

        List<ExamRow> rows = ExamRow.of(game);

        assertAll(
                () -> assertEquals(new ExamRow(FIRST, ExamRow.Status.PASSED, Optional.of(Grade.of(28))), rows.get(0)),
                () -> assertEquals(ExamRow.Status.AVAILABLE, rows.get(1).status()),
                () -> assertEquals("Superato con 28", rows.get(0).statusLabel()));
    }

    @Test
    void describesTypeAndStatusInItalian() {
        List<ExamRow> rows = ExamRow.of(game());

        assertAll(
                () -> assertEquals("Orale", rows.get(0).typeLabel()),
                () -> assertEquals("Progetto", rows.get(1).typeLabel()),
                () -> assertEquals("Da sostenere", rows.get(0).statusLabel()),
                () -> assertEquals("Boss finale: dopo tutti gli altri esami", rows.get(1).statusLabel()));
    }

    @Test
    void aGradeIsPresentOnlyForAPassedExam() {
        Optional<Grade> grade = Optional.of(Grade.of(25));

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ExamRow(FIRST, ExamRow.Status.AVAILABLE, grade)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ExamRow(FIRST, ExamRow.Status.PASSED, Optional.empty())),
                () -> assertThrows(NullPointerException.class, () -> ExamRow.of(null)));
    }
}
