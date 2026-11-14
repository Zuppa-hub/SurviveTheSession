package it.unicam.cs.mpgc.rpg127295.engine.effect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class DoubleDamageEffectTest {

    private final DoubleDamageEffect effect = new DoubleDamageEffect();

    @Test
    void canBeUsedWhenNotAlreadyActive() {
        assertTrue(effect.canBeUsedIn(States.doubled(false)));
    }

    @Test
    void doesNotStack() {
        assertFalse(effect.canBeUsedIn(States.doubled(true)));
    }

    @Test
    void doublesTheNextCorrectAnswer() {
        RecordingControls controls = new RecordingControls();

        effect.applyTo(controls);

        assertEquals(List.of("doubleNextCorrectAnswer"), controls.calls);
    }
}
