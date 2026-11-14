package it.unicam.cs.mpgc.rpg127295.engine.battle;

/**
 * The only ways an effect can change a battle. It is encapsulation: changing the battle is not
 * forbidden, it is controlled. {@link ExamBattle} implements this interface with a private
 * inner class and passes it only to effects whose use it has already checked (owned item,
 * enough concentration, effect applicable); outside an effect nobody can call these methods.
 * The amounts (seconds, energy, damage factor) come from {@link BattleRules}.
 */
public interface BattleControls {

    /** Adds {@link BattleRules#extraSeconds()} to the timer of the current question. */
    void addExtraTime();

    /** Removes two wrong options from the current question, chosen at random. */
    void removeTwoWrongOptions();

    /** Shows the hint of the current question; it counts in the performance. */
    void showHint();

    /** Restores {@link BattleRules#energyRestored()} energy, up to the maximum. */
    void restoreEnergy();

    /** Multiplies the damage of the next correct answer by {@link BattleRules#boostedDamageMultiplier()}. */
    void doubleNextCorrectAnswer();
}
