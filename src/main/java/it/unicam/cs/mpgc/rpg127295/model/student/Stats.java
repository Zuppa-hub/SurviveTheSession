package it.unicam.cs.mpgc.rpg127295.model.student;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonNegative;
import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

/**
 * The statistics of a student.
 * <p>
 * Immutable value object: every operation returns a new instance and leaves this one
 * unchanged. Energy is the student's life; concentration is the resource spent on abilities.
 *
 * @param knowledge        how much the student knows; never negative
 * @param energy           current energy, from 0 to {@code maxEnergy}
 * @param maxEnergy        maximum energy; always positive
 * @param concentration    current concentration, from 0 to {@code maxConcentration}
 * @param maxConcentration maximum concentration; never negative
 */
public record Stats(int knowledge, int energy, int maxEnergy, int concentration, int maxConcentration) {

    /** @throws IllegalArgumentException if a value is negative or above its maximum */
    public Stats {
        requireNonNegative(knowledge, "knowledge");
        requireNonNegative(energy, "energy");
        requireNonNegative(concentration, "concentration");
        requireNonNegative(maxConcentration, "maxConcentration");
        requirePositive(maxEnergy, "maxEnergy");
        requireAtMost(energy, maxEnergy, "energy");
        requireAtMost(concentration, maxConcentration, "concentration");
    }

    /** @return stats with energy and concentration at their maximum */
    public static Stats initial(int knowledge, int maxEnergy, int maxConcentration) {
        return new Stats(knowledge, maxEnergy, maxEnergy, maxConcentration, maxConcentration);
    }

    public boolean isExhausted() {
        return energy == 0;
    }

    /** @return true if at least {@code amount} concentration is available */
    public boolean canSpend(int amount) {
        requireNonNegative(amount, "amount");
        return concentration >= amount;
    }

    /** @return stats with energy reduced by {@code amount}, never below zero */
    public Stats loseEnergy(int amount) {
        requireNonNegative(amount, "amount");
        return withEnergy(Math.max(0, energy - amount));
    }

    /** @return stats with energy increased by {@code amount}, never above the maximum */
    public Stats restoreEnergy(int amount) {
        requireNonNegative(amount, "amount");
        return withEnergy(Math.min(maxEnergy, energy + amount));
    }

    public Stats restoreFullEnergy() {
        return withEnergy(maxEnergy);
    }

    /**
     * @return stats with concentration reduced by {@code amount}
     * @throws IllegalStateException if less than {@code amount} is available; check
     *                               {@link #canSpend(int)} first
     */
    public Stats spendConcentration(int amount) {
        if (!canSpend(amount)) {
            throw new IllegalStateException(
                    "Not enough concentration: " + concentration + " available, " + amount + " needed");
        }
        return withConcentration(concentration - amount);
    }

    /** @return stats with concentration increased by {@code amount}, never above the maximum */
    public Stats recoverConcentration(int amount) {
        requireNonNegative(amount, "amount");
        return withConcentration(Math.min(maxConcentration, concentration + amount));
    }

    /**
     * Applies a level-up: each bonus raises the statistic and, for energy and concentration,
     * both the maximum and the current value.
     */
    public Stats improve(int knowledgeBonus, int maxEnergyBonus, int maxConcentrationBonus) {
        requireNonNegative(knowledgeBonus, "knowledgeBonus");
        requireNonNegative(maxEnergyBonus, "maxEnergyBonus");
        requireNonNegative(maxConcentrationBonus, "maxConcentrationBonus");
        return new Stats(
                knowledge + knowledgeBonus,
                energy + maxEnergyBonus,
                maxEnergy + maxEnergyBonus,
                concentration + maxConcentrationBonus,
                maxConcentration + maxConcentrationBonus);
    }

    private Stats withEnergy(int newEnergy) {
        return new Stats(knowledge, newEnergy, maxEnergy, concentration, maxConcentration);
    }

    private Stats withConcentration(int newConcentration) {
        return new Stats(knowledge, energy, maxEnergy, newConcentration, maxConcentration);
    }

    private static void requireAtMost(int value, int max, String name) {
        if (value > max) {
            throw new IllegalArgumentException(name + " must not exceed its maximum " + max + ": " + value);
        }
    }
}
