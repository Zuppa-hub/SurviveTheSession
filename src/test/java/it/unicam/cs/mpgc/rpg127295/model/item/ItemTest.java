package it.unicam.cs.mpgc.rpg127295.model.item;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ItemTest {

    private static final Item COFFEE = new Item("coffee", "Caffè", "+15 secondi alla domanda corrente");

    @Test
    void exposesItsData() {
        assertAll(
                () -> assertEquals("coffee", COFFEE.id()),
                () -> assertEquals("Caffè", COFFEE.name()),
                () -> assertEquals("+15 secondi alla domanda corrente", COFFEE.description()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankFields(String blank) {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Item(blank, "Caffè", "Descrizione")),
                () -> assertThrows(IllegalArgumentException.class, () -> new Item("coffee", blank, "Descrizione")),
                () -> assertThrows(IllegalArgumentException.class, () -> new Item("coffee", "Caffè", blank)));
    }

    @Test
    void itemsWithSameIdAreEqualEvenWithDifferentText() {
        Item sameCoffee = new Item("coffee", "Espresso", "Un altro testo");

        assertAll(
                () -> assertEquals(COFFEE, sameCoffee),
                () -> assertEquals(COFFEE.hashCode(), sameCoffee.hashCode()));
    }

    @Test
    void itemsWithDifferentIdAreNotEqual() {
        assertNotEquals(COFFEE, new Item("energy-drink", "Caffè", "+15 secondi alla domanda corrente"));
    }

    @Test
    void toStringIsTheName() {
        assertEquals("Caffè", COFFEE.toString());
    }
}
