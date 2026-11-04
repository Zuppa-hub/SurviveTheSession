package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void hardStaysHard() {
        assertEquals(Difficulty.HARD, Difficulty.HARD.harder());
    }
}
