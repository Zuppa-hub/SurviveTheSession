package it.unicam.cs.mpgc.rpg127295.engine.effect;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choiceWithHint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.PresentedQuestion;
import java.util.List;
import org.junit.jupiter.api.Test;

class HintEffectTest {

    private final HintEffect effect = new HintEffect();

    @Test
    void canBeUsedOnAQuestionWithAHiddenHint() {
        assertTrue(effect.canBeUsedIn(States.showing(choiceWithHint("Q-1"))));
    }

    @Test
    void cannotBeUsedOnceTheHintIsShown() {
        PresentedQuestion shown = PresentedQuestion.of(choiceWithHint("Q-1"), FakeRandom.first()).withHintShown();

        assertFalse(effect.canBeUsedIn(States.showing(shown)));
    }

    @Test
    void cannotBeUsedOnAQuestionWithoutHint() {
        assertFalse(effect.canBeUsedIn(States.showing(choice("Q-1"))));
    }

    @Test
    void cannotBeUsedWithoutAQuestion() {
        assertFalse(effect.canBeUsedIn(States.doubled(false)));
    }

    @Test
    void showsTheHint() {
        RecordingControls controls = new RecordingControls();

        effect.applyTo(controls);

        assertEquals(List.of("showHint"), controls.calls);
    }
}
