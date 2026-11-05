package it.unicam.cs.mpgc.rpg127295.engine.grading;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PerformanceTest {

    @Test
    void answeredCountsCorrectAndWrongAnswers() {
        assertEquals(7, new Performance(5, 2, 1).answered());
    }

    @ParameterizedTest(name = "{3}")
    @CsvSource({
        "-1,  0,  0, negative correct answers",
        " 0, -1,  0, negative wrong answers",
        " 0,  0, -1, negative hints"
    })
    void rejectsNegativeCounts(int correct, int wrong, int hints, String reason) {
        assertThrows(IllegalArgumentException.class, () -> new Performance(correct, wrong, hints));
    }

    @Test
    void acceptsAPerformanceWithoutAnswers() {
        assertEquals(0, new Performance(0, 0, 0).answered());
    }

    @Test
    void flawlessOnlyWithoutMistakesAndHints() {
        assertAll(
                () -> assertTrue(new Performance(5, 0, 0).isFlawless()),
                () -> assertFalse(new Performance(5, 1, 0).isFlawless()),
                () -> assertFalse(new Performance(5, 0, 1).isFlawless()));
    }

    @Test
    void aPerformanceWithoutAnswersIsNotFlawless() {
        assertFalse(new Performance(0, 0, 0).isFlawless());
    }
}
