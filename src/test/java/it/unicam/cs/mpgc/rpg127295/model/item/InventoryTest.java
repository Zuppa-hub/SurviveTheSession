package it.unicam.cs.mpgc.rpg127295.model.item;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InventoryTest {

    private static final Item COFFEE = new Item("coffee", "Caffè", "+15 secondi");
    private static final Item NOTES = new Item("notes", "Appunti", "50/50");

    private Inventory inventory;

    @BeforeEach
    void createEmptyInventory() {
        inventory = new Inventory();
    }

    @Test
    void startsEmpty() {
        assertAll(
                () -> assertTrue(inventory.isEmpty()),
                () -> assertEquals(Map.of(), inventory.items()),
                () -> assertEquals(0, inventory.quantityOf(COFFEE)),
                () -> assertFalse(inventory.contains(COFFEE)));
    }

    @Test
    void addIncreasesTheQuantity() {
        inventory.add(COFFEE, 2);
        inventory.add(COFFEE, 1);

        assertAll(
                () -> assertEquals(3, inventory.quantityOf(COFFEE)),
                () -> assertTrue(inventory.contains(COFFEE)),
                () -> assertFalse(inventory.isEmpty()));
    }

    @Test
    void itemsWithSameIdShareTheQuantity() {
        inventory.add(COFFEE, 1);
        inventory.add(new Item("coffee", "Espresso", "Altro testo"), 1);

        assertEquals(2, inventory.quantityOf(COFFEE));
    }

    @Test
    void removeTakesAwayOneUnit() {
        inventory.add(COFFEE, 2);

        inventory.remove(COFFEE);

        assertEquals(1, inventory.quantityOf(COFFEE));
    }

    @Test
    void removingTheLastUnitRemovesTheEntry() {
        inventory.add(COFFEE, 1);
        inventory.add(NOTES, 1);

        inventory.remove(COFFEE);

        assertAll(
                () -> assertFalse(inventory.contains(COFFEE)),
                () -> assertEquals(Map.of(NOTES, 1), inventory.items()));
    }

    @Test
    void removingAnAbsentItemIsAnError() {
        assertThrows(IllegalStateException.class, () -> inventory.remove(COFFEE));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
    void rejectsNonPositiveQuantities(int quantity) {
        assertThrows(IllegalArgumentException.class, () -> inventory.add(COFFEE, quantity));
    }

    @Test
    void rejectsNullItems() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> inventory.add(null, 1)),
                () -> assertThrows(NullPointerException.class, () -> inventory.remove(null)));
    }

    @Test
    void itemsIsAnUnmodifiableSnapshot() {
        inventory.add(COFFEE, 1);
        Map<Item, Integer> snapshot = inventory.items();

        inventory.add(NOTES, 1);

        assertAll(
                () -> assertEquals(Map.of(COFFEE, 1), snapshot),
                () -> assertThrows(UnsupportedOperationException.class, () -> snapshot.put(NOTES, 5)));
    }
}
