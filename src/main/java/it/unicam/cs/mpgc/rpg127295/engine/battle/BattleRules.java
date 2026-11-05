package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;

/**
 * The numeric rules of a battle, kept in one place so that balancing does not touch the
 * battle flow or the strategies.
 * <p>
 * Contract: every value returned is positive; null arguments throw
 * {@link NullPointerException}.
 */
public interface BattleRules {

    /** @return the resistance of the exam at the start of the battle */
    int resistanceOf(Exam exam);

    /** @return the resistance removed by a correct answer of a student with these stats */
    int damageOf(Stats stats);

    /** @return the energy lost for a mistake, before the multiplier of the exam strategy */
    int baseEnergyLoss(Difficulty difficulty);

    /** @return the concentration recovered with a correct answer */
    int concentrationPerCorrectAnswer();
}
