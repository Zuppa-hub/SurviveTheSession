package it.unicam.cs.mpgc.rpg127295.engine.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleOutcome;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import org.junit.jupiter.api.Test;

class ExamReportTest {

    private static final Exam EXAM = new Exam("PROG1", TestQuestions.SUBJECT, ExamType.ORAL, 1, Difficulty.EASY);
    private static final BattleOutcome RETIRED = BattleOutcome.retired(Performance.none());

    @Test
    void rejectsInvalidArguments() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new ExamReport(EXAM, RETIRED, Reward.none(), -1)),
                () -> assertThrows(NullPointerException.class, () -> new ExamReport(null, RETIRED, Reward.none(), 0)),
                () -> assertThrows(NullPointerException.class, () -> new ExamReport(EXAM, null, Reward.none(), 0)),
                () -> assertThrows(NullPointerException.class, () -> new ExamReport(EXAM, RETIRED, null, 0)));
    }
}
