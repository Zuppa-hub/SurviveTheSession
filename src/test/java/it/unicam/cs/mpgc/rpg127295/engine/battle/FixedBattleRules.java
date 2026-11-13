package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;

/** Rules for the tests, with fixed values that do not depend on exam or stats. */
record FixedBattleRules(int resistance, int damage, int energyLoss, int concentration) implements BattleRules {

    /** Resistance 10, damage 5 (two correct answers win), energy loss 2, concentration 1. */
    static FixedBattleRules standard() {
        return new FixedBattleRules(10, 5, 2, 1);
    }

    @Override
    public int resistanceOf(Exam exam) {
        return resistance;
    }

    @Override
    public int damageOf(Stats stats) {
        return damage;
    }

    @Override
    public int baseEnergyLoss(Difficulty difficulty) {
        return energyLoss;
    }

    @Override
    public int concentrationPerCorrectAnswer() {
        return concentration;
    }

    /** 15, as in the standard rules. */
    @Override
    public int extraSeconds() {
        return 15;
    }

    /** 3: less than the energy lost by two mistakes, so the tests can see the cap. */
    @Override
    public int energyRestored() {
        return 3;
    }

    @Override
    public int boostedDamageMultiplier() {
        return 2;
    }
}
