package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StandardBattleRulesTest {

    private final BattleRules rules = new StandardBattleRules();

    @ParameterizedTest(name = "{0} CFU, {1} -> {2}")
    @CsvSource({
        "12, MEDIUM, 36",
        " 6, EASY,   12",
        " 9, HARD,   36",
        " 1, EASY,    2"
    })
    void resistanceIsCreditsTimesTheFactorOfTheDifficulty(int credits, Difficulty difficulty, int expected) {
        Exam exam = new Exam("E1", TestQuestions.SUBJECT, ExamType.WRITTEN, credits, difficulty);

        assertEquals(expected, rules.resistanceOf(exam));
    }

    @ParameterizedTest(name = "knowledge {0} -> {1}")
    @CsvSource({
        "0, 4",
        "3, 5",
        "5, 6",
        "8, 8"
    })
    void damageGrowsWithKnowledge(int knowledge, int expected) {
        assertEquals(expected, rules.damageOf(Stats.initial(knowledge, 10, 4)));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
        "EASY,   1",
        "MEDIUM, 2",
        "HARD,   3"
    })
    void energyLossGrowsWithDifficulty(Difficulty difficulty, int expected) {
        assertEquals(expected, rules.baseEnergyLoss(difficulty));
    }

    @Test
    void aCorrectAnswerRecoversOneConcentrationPoint() {
        assertEquals(1, rules.concentrationPerCorrectAnswer());
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> rules.resistanceOf(null)),
                () -> assertThrows(NullPointerException.class, () -> rules.damageOf(null)),
                () -> assertThrows(NullPointerException.class, () -> rules.baseEnergyLoss(null)));
    }
}
