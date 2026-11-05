package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BattleStateTest {

    private static final Stats STATS = Stats.initial(5, 10, 4);
    private static final Performance NONE = new Performance(0, 0, 0);

    private static BattleState state(int resistance, int maxResistance) {
        return new BattleState(resistance, maxResistance, STATS, NONE, Optional.empty(), Optional.empty());
    }

    @Test
    void theExamIsDefeatedOnlyAtZeroResistance() {
        assertAll(
                () -> assertTrue(state(0, 36).isExamDefeated()),
                () -> assertFalse(state(1, 36).isExamDefeated()));
    }

    @ParameterizedTest(name = "resistance {0} of {1}")
    @CsvSource({
        "-1, 36",
        "37, 36",
        " 0,  0"
    })
    void rejectsResistanceOutOfRange(int resistance, int maxResistance) {
        assertThrows(IllegalArgumentException.class, () -> state(resistance, maxResistance));
    }

    @Test
    void rejectsNullArguments() {
        Optional<PresentedQuestion> noQuestion = Optional.empty();
        Optional<AnswerResult> noAnswer = Optional.empty();

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new BattleState(1, 1, null, NONE, noQuestion, noAnswer)),
                () -> assertThrows(NullPointerException.class,
                        () -> new BattleState(1, 1, STATS, null, noQuestion, noAnswer)),
                () -> assertThrows(NullPointerException.class,
                        () -> new BattleState(1, 1, STATS, NONE, null, noAnswer)),
                () -> assertThrows(NullPointerException.class,
                        () -> new BattleState(1, 1, STATS, NONE, noQuestion, null)),
                () -> assertThrows(NullPointerException.class, () -> new AnswerResult(null, true)));
    }

    @Test
    void anAnswerResultKeepsItsQuestion() {
        AnswerResult result = new AnswerResult(choice("Q-1"), false);

        assertAll(
                () -> assertFalse(result.correct()),
                () -> assertEquals(choice("Q-1"), result.question()));
    }
}
