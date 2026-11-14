package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator;
import it.unicam.cs.mpgc.rpg127295.engine.timer.QuestionTimer;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * The collaborators shared by every battle, grouped so that they travel together from the
 * application to the factory and to each battle (parameter object).
 *
 * @param rules      the numeric rules
 * @param calculator decides the grade at the end
 * @param timer      the countdown of timed questions
 * @param random     shuffles options and chooses questions
 * @param effects    the effects of the items and the abilities
 */
public record BattleServices(BattleRules rules, GradeCalculator calculator, QuestionTimer timer,
                             RandomGenerator random, BattleEffects effects) {

    public BattleServices {
        Objects.requireNonNull(rules, "Battle rules must not be null");
        Objects.requireNonNull(calculator, "Grade calculator must not be null");
        Objects.requireNonNull(timer, "Question timer must not be null");
        Objects.requireNonNull(random, "Random generator must not be null");
        Objects.requireNonNull(effects, "Battle effects must not be null");
    }
}
