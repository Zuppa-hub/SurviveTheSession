package it.unicam.cs.mpgc.rpg127295.engine.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StandardProgressionRulesTest {

    private final ProgressionRules rules = new StandardProgressionRules();

    @ParameterizedTest(name = "level {0} -> {1} experience")
    @CsvSource({
        "1, 100",
        "2, 200",
        "5, 500"
    })
    void levellingUpCostsAHundredTimesTheCurrentLevel(int level, int expected) {
        assertEquals(expected, rules.experienceToLevelUp(level));
    }

    @Test
    void eachLevelImprovesKnowledgeEnergyAndConcentration() {
        assertAll(
                () -> assertEquals(1, rules.knowledgeBonus()),
                () -> assertEquals(2, rules.maxEnergyBonus()),
                () -> assertEquals(1, rules.maxConcentrationBonus()));
    }

    @Test
    void rejectsALevelBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> rules.experienceToLevelUp(0));
    }
}
