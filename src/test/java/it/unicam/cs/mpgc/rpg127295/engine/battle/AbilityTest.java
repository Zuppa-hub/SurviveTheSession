package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AbilityTest {

    static final BattleEffect NO_EFFECT = new BattleEffect() {
        @Override
        public boolean canBeUsedIn(BattleState state) {
            return true;
        }

        @Override
        public void applyTo(BattleControls controls) {
        }
    };

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsACostThatIsNotPositive(int cost) {
        assertThrows(IllegalArgumentException.class, () -> new Ability("Ripasso lampo", cost, NO_EFFECT));
    }

    @Test
    void rejectsInvalidArguments() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Ability(" ", 2, NO_EFFECT)),
                () -> assertThrows(NullPointerException.class, () -> new Ability(null, 2, NO_EFFECT)),
                () -> assertThrows(NullPointerException.class, () -> new Ability("Ripasso lampo", 2, null)));
    }
}
