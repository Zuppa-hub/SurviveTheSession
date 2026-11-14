package it.unicam.cs.mpgc.rpg127295.engine.game;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;

/** Decides the reward for a passed exam. */
@FunctionalInterface
public interface RewardPolicy {

    Reward rewardFor(Exam exam, Grade grade);
}
