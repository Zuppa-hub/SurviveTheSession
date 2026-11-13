package it.unicam.cs.mpgc.rpg127295.engine.effect;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleControls;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffect;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;

/** The next correct answer does boosted damage (Stroke of genius); it does not stack. */
public final class DoubleDamageEffect implements BattleEffect {

    @Override
    public boolean canBeUsedIn(BattleState state) {
        return !state.nextCorrectAnswerDoubled();
    }

    @Override
    public void applyTo(BattleControls controls) {
        controls.doubleNextCorrectAnswer();
    }
}
