package it.unicam.cs.mpgc.rpg127295.model.student;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class LevelTest {

    @Test
    void initialLevelIsOneWithoutExperience() {
        assertEquals(new Level(1, 0), Level.initial());
    }

    @ParameterizedTest(name = "{2}")
    @CsvSource({
        " 0,  0, level zero",
        "-1,  0, negative level",
        " 1, -1, negative experience"
    })
    void rejectsInvalidValues(int number, int experience, String reason) {
        assertThrows(IllegalArgumentException.class, () -> new Level(number, experience));
    }

    @Test
    void addExperienceKeepsTheLevel() {
        assertEquals(new Level(2, 35), new Level(2, 10).addExperience(25));
    }

    @Test
    void addingZeroExperienceChangesNothing() {
        assertEquals(new Level(2, 10), new Level(2, 10).addExperience(0));
    }

    @Test
    void rejectsNegativeExperienceGain() {
        assertThrows(IllegalArgumentException.class, () -> Level.initial().addExperience(-1));
    }

    @Test
    void levelUpSpendsTheCostAndKeepsTheRest() {
        assertEquals(new Level(3, 15), new Level(2, 115).levelUp(100));
    }

    @Test
    void levelUpWithExactlyTheCostLeavesNoExperience() {
        assertEquals(new Level(3, 0), new Level(2, 100).levelUp(100));
    }

    @Test
    void levelUpRequiresEnoughExperience() {
        assertThrows(IllegalArgumentException.class, () -> new Level(2, 99).levelUp(100));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
    void levelUpRequiresAPositiveCost(int cost) {
        assertThrows(IllegalArgumentException.class, () -> new Level(2, 50).levelUp(cost));
    }

    @Test
    void operationsDoNotModifyTheOriginal() {
        Level level = new Level(2, 100);

        level.addExperience(10);
        level.levelUp(50);

        assertAll(
                () -> assertEquals(2, level.number()),
                () -> assertEquals(100, level.experience()));
    }
}
