package it.unicam.cs.mpgc.rpg127295.engine.game;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;

/** Decides the reward for a passed exam. */
@FunctionalInterface
public interface RewardPolicy {

    /**
     * Must answer for any exam of the session and any grade, so that the rules can be asked
     * about a grade not obtained yet (as the "how to play" pages do).
     *
     * @return the reward, never null; {@link Reward#none()} if there is none
     * @throws NullPointerException if an argument is null
     */
    Reward rewardFor(Exam exam, Grade grade);
}
