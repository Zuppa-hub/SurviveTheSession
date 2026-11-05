package it.unicam.cs.mpgc.rpg127295.engine.effect;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.fillIn;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.trueFalse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.PresentedQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.List;
import org.junit.jupiter.api.Test;

class FiftyFiftyEffectTest {

    private final FiftyFiftyEffect effect = new FiftyFiftyEffect();

    @Test
    void canBeUsedWithAtLeastThreeWrongOptionsShown() {
        assertTrue(effect.canBeUsedIn(States.showing(choice("Q-1"))));
    }

    @Test
    void cannotBeUsedTwiceOnTheSameQuestion() {
        PresentedQuestion halved = PresentedQuestion.of(choice("Q-1"), FakeRandom.first())
                .withoutWrongOptions(2, FakeRandom.first());

        assertFalse(effect.canBeUsedIn(States.showing(halved)));
    }

    @Test
    void cannotBeUsedOnTrueFalseOrFillInQuestions() {
        assertAll(
                () -> assertFalse(effect.canBeUsedIn(States.showing(trueFalse("Q-1")))),
                () -> assertFalse(effect.canBeUsedIn(States.showing(fillIn("Q-2", Difficulty.EASY)))));
    }

    @Test
    void cannotBeUsedWithoutAQuestion() {
        assertFalse(effect.canBeUsedIn(States.doubled(false)));
    }

    @Test
    void removesTwoWrongOptions() {
        RecordingControls controls = new RecordingControls();

        effect.applyTo(controls);

        assertEquals(List.of("removeTwoWrongOptions"), controls.calls);
    }
}
