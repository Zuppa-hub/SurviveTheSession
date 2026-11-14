package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SlotNamesTest {

    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @CsvSource(delimiter = '|', value = {
        "Andrea                 | andrea",
        "Andrea Cazzato         | andrea-cazzato",
        "'  Àndrea  Cazzàto! '  | andrea-cazzato",
        "studente_2             | studente_2",
        "???                    | partita"
    })
    void turnsThePlayerNameIntoAValidSlot(String name, String expected) {
        String slot = SlotNames.fromPlayerName(name);

        assertEquals(expected, slot);
        assertDoesNotThrow(() -> SaveGameRepository.requireValidSlot(slot));
    }

    @ParameterizedTest
    @CsvSource(nullValues = "null", value = "null")
    void rejectsNull(String name) {
        assertThrows(NullPointerException.class, () -> SlotNames.fromPlayerName(name));
    }
}
