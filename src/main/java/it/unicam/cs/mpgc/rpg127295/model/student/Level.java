package it.unicam.cs.mpgc.rpg127295.model.student;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonNegative;
import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

/**
 * The student's level and the experience accumulated in it. Immutable value object.
 * <p>
 * It knows how to change in a valid way but not when: experience thresholds and level-up
 * bonuses are balancing rules owned by the engine, which passes the cost to
 * {@link #levelUp(int)}.
 *
 * @param number     the level, starting from 1
 * @param experience experience accumulated in the current level; never negative
 */
public record Level(int number, int experience) {

    /** @throws IllegalArgumentException if number is below 1 or experience is negative */
    public Level {
        requirePositive(number, "Level number");
        requireNonNegative(experience, "Experience");
    }

    /** @return level 1 with no experience */
    public static Level initial() {
        return new Level(1, 0);
    }

    /**
     * @return the same level with {@code amount} more experience
     * @throws IllegalArgumentException if amount is negative
     */
    public Level addExperience(int amount) {
        requireNonNegative(amount, "Experience gained");
        return new Level(number, experience + amount);
    }

    /**
     * @param cost the experience needed to leave the current level, decided by the engine
     * @return the next level, keeping the experience left over after paying the cost
     * @throws IllegalArgumentException if cost is not positive or exceeds the experience
     */
    public Level levelUp(int cost) {
        requirePositive(cost, "Level-up cost");
        if (cost > experience) {
            throw new IllegalArgumentException(
                    "Not enough experience to level up: " + experience + " available, " + cost + " needed");
        }
        return new Level(number + 1, experience - cost);
    }
}
