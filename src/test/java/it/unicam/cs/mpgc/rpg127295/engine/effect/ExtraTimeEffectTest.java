package it.unicam.cs.mpgc.rpg127295.engine.effect;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExtraTimeEffectTest {

    private final ExtraTimeEffect effect = new ExtraTimeEffect();

    @Test
    void canBeUsedOnlyOnATimedQuestion() {
        assertAll(
                () -> assertTrue(effect.canBeUsedIn(States.timed(true))),
                () -> assertFalse(effect.canBeUsedIn(States.timed(false))));
    }

    @Test
    void addsExtraTime() {
        RecordingControls controls = new RecordingControls();

        effect.applyTo(controls);

        assertEquals(List.of("addExtraTime"), controls.calls);
    }
}
