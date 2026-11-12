package it.unicam.cs.mpgc.rpg127295.engine.game;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

/**
 * Provisional progression (final balancing in issue #12): leaving level n costs 100 x n
 * experience; each level gives +1 knowledge, +2 max energy and +1 max concentration.
 */
public final class StandardProgressionRules implements ProgressionRules {

    private static final int EXPERIENCE_PER_LEVEL = 100;
    private static final int KNOWLEDGE_BONUS = 1;
    private static final int MAX_ENERGY_BONUS = 2;
    private static final int MAX_CONCENTRATION_BONUS = 1;

    /** @throws IllegalArgumentException if the level is below 1 */
    @Override
    public int experienceToLevelUp(int levelNumber) {
        requirePositive(levelNumber, "Level number");
        return EXPERIENCE_PER_LEVEL * levelNumber;
    }

    @Override
    public int knowledgeBonus() {
        return KNOWLEDGE_BONUS;
    }

    @Override
    public int maxEnergyBonus() {
        return MAX_ENERGY_BONUS;
    }

    @Override
    public int maxConcentrationBonus() {
        return MAX_CONCENTRATION_BONUS;
    }
}
