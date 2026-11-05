package it.unicam.cs.mpgc.rpg127295.engine.effect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class EnergyRestoreEffectTest {

    private final EnergyRestoreEffect effect = new EnergyRestoreEffect();

    @Test
    void canBeUsedBelowFullEnergy() {
        assertTrue(effect.canBeUsedIn(States.withStats(States.FULL.loseEnergy(1))));
    }

    @Test
    void cannotBeUsedAtFullEnergy() {
        assertFalse(effect.canBeUsedIn(States.withStats(States.FULL)));
    }

    @Test
    void restoresEnergy() {
        RecordingControls controls = new RecordingControls();

        effect.applyTo(controls);

        assertEquals(List.of("restoreEnergy"), controls.calls);
    }
}
