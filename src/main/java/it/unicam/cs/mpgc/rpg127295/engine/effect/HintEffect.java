package it.unicam.cs.mpgc.rpg127295.engine.effect;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleControls;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffect;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;

/**
 * Shows the hint of the current question (Summary and Quick review); only if it has one that
 * is still hidden, so a hint counts once per question.
 */
public final class HintEffect implements BattleEffect {

    @Override
    public boolean canBeUsedIn(BattleState state) {
        return state.currentQuestion()
                .filter(question -> !question.hintVisible())
                .map(question -> question.question().hint().isPresent())
                .orElse(false);
    }

    @Override
    public void applyTo(BattleControls controls) {
        controls.showHint();
    }
}
