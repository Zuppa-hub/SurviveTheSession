package it.unicam.cs.mpgc.rpg127295.ui;

import static it.unicam.cs.mpgc.rpg127295.ui.TestGames.COFFEE;
import static it.unicam.cs.mpgc.rpg127295.ui.TestGames.NOTES;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleOutcome;
import it.unicam.cs.mpgc.rpg127295.engine.game.ExamReport;
import it.unicam.cs.mpgc.rpg127295.engine.game.Reward;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OutcomeSummaryTest {

    private static final Exam EXAM = new Exam("PROG1", TestQuestions.SUBJECT, ExamType.WRITTEN, 12, Difficulty.MEDIUM);
    private static final Performance PERFORMANCE = new Performance(5, 1, 2);

    @Test
    void aPassedExamShowsGradeAnswersAndReward() {
        Reward reward = new Reward(120, Map.of(COFFEE, 2, NOTES, 1));
        ExamReport report = new ExamReport(EXAM, BattleOutcome.passed(Grade.of(31), PERFORMANCE), reward, 1);

        OutcomeSummary summary = OutcomeSummary.of(report);

        assertAll(
                () -> assertEquals("Esame superato", summary.title()),
                () -> assertEquals(Optional.of("30 e lode"), summary.grade()),
                () -> assertEquals(List.of(
                        "Risposte giuste 5 · sbagliate 1 · suggerimenti 2",
                        "Esperienza +120",
                        "Ricevi Appunti × 1",
                        "Ricevi Caffè × 2",
                        "Livello aumentato di 1"), summary.details()));
    }

    @Test
    void aPassedExamWithoutRewardShowsOnlyTheAnswers() {
        ExamReport report = new ExamReport(EXAM, BattleOutcome.passed(Grade.of(24), PERFORMANCE), Reward.none(), 0);

        assertEquals(List.of("Risposte giuste 5 · sbagliate 1 · suggerimenti 2"), OutcomeSummary.of(report).details());
    }

    @Test
    void aFailedOrAbandonedExamHasNoGradeAndStaysInTheSession() {
        OutcomeSummary failed = OutcomeSummary.of(
                new ExamReport(EXAM, BattleOutcome.failed(PERFORMANCE), Reward.none(), 0));
        OutcomeSummary retired = OutcomeSummary.of(
                new ExamReport(EXAM, BattleOutcome.retired(PERFORMANCE), Reward.none(), 0));

        assertAll(
                () -> assertEquals("Esame non superato", failed.title()),
                () -> assertEquals("Ritiro dall'esame", retired.title()),
                () -> assertEquals(Optional.empty(), failed.grade()),
                () -> assertEquals(List.of(
                        "Risposte giuste 5 · sbagliate 1 · suggerimenti 2",
                        "L'esame resta nella sessione: puoi riprovarlo."), retired.details()));
    }

    @Test
    void rejectsAMissingReport() {
        assertThrows(NullPointerException.class, () -> OutcomeSummary.of(null));
    }
}
