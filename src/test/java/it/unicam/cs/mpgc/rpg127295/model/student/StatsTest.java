package it.unicam.cs.mpgc.rpg127295.model.student;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StatsTest {

    private static final Stats FULL = Stats.initial(5, 10, 4);

    @Nested
    class Validation {

        @ParameterizedTest(name = "{5}")
        @CsvSource({
            "-1, 10, 10,  4,  4, negative knowledge",
            " 5, -1, 10,  4,  4, negative energy",
            " 5,  0,  0,  4,  4, zero maxEnergy",
            " 5, 11, 10,  4,  4, energy above max",
            " 5, 10, 10, -1,  4, negative concentration",
            " 5, 10, 10,  0, -1, negative maxConcentration",
            " 5, 10, 10,  5,  4, concentration above max"
        })
        void rejectsInvalidValues(int knowledge, int energy, int maxEnergy,
                                  int concentration, int maxConcentration, String reason) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Stats(knowledge, energy, maxEnergy, concentration, maxConcentration));
        }

        @Test
        void acceptsZeroEnergyAndConcentration() {
            Stats stats = new Stats(0, 0, 10, 0, 0);

            assertEquals(0, stats.energy());
            assertEquals(0, stats.concentration());
        }

        @Test
        void rejectsNegativeAmounts() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> FULL.loseEnergy(-1)),
                    () -> assertThrows(IllegalArgumentException.class, () -> FULL.restoreEnergy(-1)),
                    () -> assertThrows(IllegalArgumentException.class, () -> FULL.spendConcentration(-1)),
                    () -> assertThrows(IllegalArgumentException.class, () -> FULL.recoverConcentration(-1)),
                    () -> assertThrows(IllegalArgumentException.class, () -> FULL.canSpend(-1)),
                    () -> assertThrows(IllegalArgumentException.class, () -> FULL.improve(-1, 0, 0)),
                    () -> assertThrows(IllegalArgumentException.class, () -> FULL.improve(0, -1, 0)),
                    () -> assertThrows(IllegalArgumentException.class, () -> FULL.improve(0, 0, -1)));
        }
    }

    @Test
    void initialStatsStartFull() {
        assertEquals(new Stats(5, 10, 10, 4, 4), FULL);
    }

    @Nested
    class Energy {

        @Test
        void loseEnergyReducesEnergy() {
            assertEquals(7, FULL.loseEnergy(3).energy());
        }

        @Test
        void loseEnergyStopsAtZero() {
            assertEquals(0, FULL.loseEnergy(25).energy());
        }

        @Test
        void exhaustedWhenEnergyIsZero() {
            assertTrue(FULL.loseEnergy(10).isExhausted());
            assertFalse(FULL.loseEnergy(9).isExhausted());
        }

        @Test
        void restoreEnergyIncreasesEnergyUpToMax() {
            Stats tired = FULL.loseEnergy(6);

            assertEquals(7, tired.restoreEnergy(3).energy());
            assertEquals(10, tired.restoreEnergy(50).energy());
        }

        @Test
        void restoreFullEnergyRefillsToMax() {
            assertEquals(10, FULL.loseEnergy(9).restoreFullEnergy().energy());
        }
    }

    @Nested
    class Concentration {

        @Test
        void spendConcentrationReducesConcentration() {
            assertEquals(1, FULL.spendConcentration(3).concentration());
        }

        @Test
        void canSpendOnlyWhatIsAvailable() {
            assertTrue(FULL.canSpend(4));
            assertFalse(FULL.canSpend(5));
        }

        @Test
        void spendingMoreThanAvailableIsAnError() {
            assertThrows(IllegalStateException.class, () -> FULL.spendConcentration(5));
        }

        @Test
        void recoverConcentrationStopsAtMax() {
            Stats spent = FULL.spendConcentration(3);

            assertEquals(2, spent.recoverConcentration(1).concentration());
            assertEquals(4, spent.recoverConcentration(10).concentration());
        }
    }

    @Test
    void improveRaisesKnowledgeAndMaximumsTogetherWithCurrentValues() {
        Stats improved = FULL.loseEnergy(4).spendConcentration(1).improve(2, 3, 1);

        assertEquals(new Stats(7, 9, 13, 4, 5), improved);
    }

    @Test
    void operationsDoNotModifyTheOriginal() {
        FULL.loseEnergy(5);
        FULL.spendConcentration(2);
        FULL.improve(1, 1, 1);

        assertEquals(new Stats(5, 10, 10, 4, 4), FULL);
    }

    @Test
    void statsWithDifferentValuesAreNotEqual() {
        assertNotEquals(FULL, FULL.loseEnergy(1));
    }
}
