package it.unicam.cs.mpgc.rpg127295.engine.effect;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleControls;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffect;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;

/** More time for the current question (Coffee); only for a question with a timer. */
public final class ExtraTimeEffect implements BattleEffect {

    @Override
    public boolean canBeUsedIn(BattleState state) {
        return state.timed();
    }

    @Override
    public void applyTo(BattleControls controls) {
        controls.addExtraTime();
    }
}
