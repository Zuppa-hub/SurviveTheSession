package it.unicam.cs.mpgc.rpg127295.engine.effect;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleControls;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffect;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;

/** Restores energy (Energy drink); not at full energy, so the item is not wasted. */
public final class EnergyRestoreEffect implements BattleEffect {

    @Override
    public boolean canBeUsedIn(BattleState state) {
        return state.stats().energy() < state.stats().maxEnergy();
    }

    @Override
    public void applyTo(BattleControls controls) {
        controls.restoreEnergy();
    }
}
