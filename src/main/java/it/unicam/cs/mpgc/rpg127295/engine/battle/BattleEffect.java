package it.unicam.cs.mpgc.rpg127295.engine.battle;

/**
 * What an item or an ability does during a battle. Items are associated with an effect by a
 * map built by the application; an {@link Ability} holds its effect. The same effect can serve
 * both, such as showing the hint (Summary and Quick review).
 * <p>
 * Defined here, next to {@link ExamBattle} that uses it; the implementations are in
 * {@code engine.effect}, which depends on this package and not the other way round (DIP).
 */
public interface BattleEffect {

    /** @return true if the effect would change something in this state */
    boolean canBeUsedIn(BattleState state);

    /** Applies the effect; called only if {@link #canBeUsedIn} is true. */
    void applyTo(BattleControls controls);
}
