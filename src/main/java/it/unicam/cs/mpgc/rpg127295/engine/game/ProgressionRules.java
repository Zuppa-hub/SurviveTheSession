package it.unicam.cs.mpgc.rpg127295.engine.game;

/**
 * When the student levels up and what each level gives. The model knows how a level changes
 * ({@code Level.levelUp}, {@code Stats.improve}); these rules decide when and by how much.
 * <p>
 * Contract: every value returned is positive.
 */
public interface ProgressionRules {

    /**
     * @param levelNumber the current level, at least 1
     * @return the experience needed to leave that level
     */
    int experienceToLevelUp(int levelNumber);

    int knowledgeBonus();

    int maxEnergyBonus();

    int maxConcentrationBonus();
}
