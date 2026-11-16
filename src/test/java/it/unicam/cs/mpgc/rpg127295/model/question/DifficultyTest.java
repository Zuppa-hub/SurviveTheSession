package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DifficultyTest {

    @Test
    void easyBecomesMedium() {
        assertEquals(Difficulty.MEDIUM, Difficulty.EASY.harder());
    }

    @Test
    void mediumBecomesHard() {
        assertEquals(Difficulty.HARD, Difficulty.MEDIUM.harder());
    }

    @Test
    void theDistanceCountsTheStepsBetweenTwoDifficulties() {
        assertAll(
                () -> assertEquals(0, Difficulty.MEDIUM.distanceFrom(Difficulty.MEDIUM)),
                () -> assertEquals(1, Difficulty.EASY.distanceFrom(Difficulty.MEDIUM)),
                () -> assertEquals(2, Difficulty.HARD.distanceFrom(Difficulty.EASY)),
                () -> assertThrows(NullPointerException.class, () -> Difficulty.EASY.distanceFrom(null)));
    }

    @Test
    void hardStaysHard() {
        assertEquals(Difficulty.HARD, Difficulty.HARD.harder());
    }
}
