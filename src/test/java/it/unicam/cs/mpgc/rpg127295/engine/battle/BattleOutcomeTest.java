package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BattleOutcomeTest {

    private static final Performance PERFORMANCE = new Performance(5, 1, 0);

    @Test
    void aPassedExamHasAGrade() {
        BattleOutcome outcome = BattleOutcome.passed(Grade.of(27), PERFORMANCE);

        assertAll(
                () -> assertEquals(BattleResult.PASSED, outcome.result()),
                () -> assertEquals(Optional.of(Grade.of(27)), outcome.grade()),
                () -> assertEquals(PERFORMANCE, outcome.performance()));
    }

    @Test
    void failedAndRetiredExamsHaveNoGrade() {
        assertAll(
                () -> assertEquals(new BattleOutcome(BattleResult.FAILED, Optional.empty(), PERFORMANCE),
                        BattleOutcome.failed(PERFORMANCE)),
                () -> assertEquals(new BattleOutcome(BattleResult.RETIRED, Optional.empty(), PERFORMANCE),
                        BattleOutcome.retired(PERFORMANCE)));
    }

    @ParameterizedTest
    @EnumSource(value = BattleResult.class, names = {"FAILED", "RETIRED"})
    void rejectsAGradeWithoutPassing(BattleResult result) {
        Optional<Grade> grade = Optional.of(Grade.of(18));

        assertThrows(IllegalArgumentException.class, () -> new BattleOutcome(result, grade, PERFORMANCE));
    }

    @Test
    void rejectsPassingWithoutAGrade() {
        assertThrows(IllegalArgumentException.class,
                () -> new BattleOutcome(BattleResult.PASSED, Optional.empty(), PERFORMANCE));
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new BattleOutcome(null, Optional.empty(), PERFORMANCE)),
                () -> assertThrows(NullPointerException.class,
                        () -> new BattleOutcome(BattleResult.FAILED, null, PERFORMANCE)),
                () -> assertThrows(NullPointerException.class,
                        () -> new BattleOutcome(BattleResult.FAILED, Optional.empty(), null)));
    }
}
