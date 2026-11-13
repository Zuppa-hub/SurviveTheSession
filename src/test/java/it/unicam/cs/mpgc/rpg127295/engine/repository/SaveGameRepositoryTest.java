package it.unicam.cs.mpgc.rpg127295.engine.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class SaveGameRepositoryTest {

    @ParameterizedTest
    @ValueSource(strings = {"andrea", "Partita-1", "slot_2", "A"})
    void acceptsLettersDigitsDashesAndUnderscores(String slot) {
        assertEquals(slot, SaveGameRepository.requireValidSlot(slot));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "../andrea", "a b", "partita.json", "andrè", "a/b"})
    void rejectsAnyOtherName(String slot) {
        assertThrows(IllegalArgumentException.class, () -> SaveGameRepository.requireValidSlot(slot));
    }

    @ParameterizedTest
    @NullSource
    void rejectsNull(String slot) {
        assertThrows(NullPointerException.class, () -> SaveGameRepository.requireValidSlot(slot));
    }
}
