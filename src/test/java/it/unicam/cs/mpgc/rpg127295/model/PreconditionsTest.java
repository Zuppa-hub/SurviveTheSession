package it.unicam.cs.mpgc.rpg127295.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PreconditionsTest {

    @Test
    void nonBlankReturnsTheValue() {
        assertEquals("PROG", Preconditions.requireNonBlank("PROG", "code"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void nonBlankRejectsBlankValues(String value) {
        assertThrows(IllegalArgumentException.class, () -> Preconditions.requireNonBlank(value, "code"));
    }

    @Test
    void messageNamesTheArgument() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Preconditions.requireNonBlank("", "Exam code"));

        assertTrue(error.getMessage().contains("Exam code"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 12, Integer.MAX_VALUE})
    void positiveReturnsTheValue(int value) {
        assertEquals(value, Preconditions.requirePositive(value, "credits"));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
    void positiveRejectsZeroAndNegatives(int value) {
        assertThrows(IllegalArgumentException.class, () -> Preconditions.requirePositive(value, "credits"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
    void nonNegativeReturnsTheValue(int value) {
        assertEquals(value, Preconditions.requireNonNegative(value, "energy"));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1})
    void nonNegativeRejectsNegatives(int value) {
        assertThrows(IllegalArgumentException.class, () -> Preconditions.requireNonNegative(value, "energy"));
    }
}
