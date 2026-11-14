package it.unicam.cs.mpgc.rpg127295.engine.effect;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleControls;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffect;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;

/**
 * Removes two wrong options (Notes). Applicable only if at least three wrong options are
 * shown, so that one is left: never on true/false or fill-in questions, and never twice on
 * the same question.
 */
public final class FiftyFiftyEffect implements BattleEffect {

    private static final int MIN_WRONG_OPTIONS = 3;

    @Override
    public boolean canBeUsedIn(BattleState state) {
        return state.currentQuestion()
                .map(question -> question.wrongOptions().size() >= MIN_WRONG_OPTIONS)
                .orElse(false);
    }

    @Override
    public void applyTo(BattleControls controls) {
        controls.removeTwoWrongOptions();
    }
}
