package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualQuestionTimer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BattleServicesTest {

    private static final BattleRules RULES = FixedBattleRules.standard();
    private static final GradeCalculator CALCULATOR = performance -> Optional.empty();
    private static final ManualQuestionTimer TIMER = new ManualQuestionTimer();

    @Test
    void everyServiceIsRequired() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new BattleServices(null, CALCULATOR, TIMER,
                        FakeRandom.first(), BattleEffects.none())),
                () -> assertThrows(NullPointerException.class, () -> new BattleServices(RULES, null, TIMER,
                        FakeRandom.first(), BattleEffects.none())),
                () -> assertThrows(NullPointerException.class, () -> new BattleServices(RULES, CALCULATOR, null,
                        FakeRandom.first(), BattleEffects.none())),
                () -> assertThrows(NullPointerException.class, () -> new BattleServices(RULES, CALCULATOR, TIMER,
                        null, BattleEffects.none())),
                () -> assertThrows(NullPointerException.class, () -> new BattleServices(RULES, CALCULATOR, TIMER,
                        FakeRandom.first(), null)));
    }
}
