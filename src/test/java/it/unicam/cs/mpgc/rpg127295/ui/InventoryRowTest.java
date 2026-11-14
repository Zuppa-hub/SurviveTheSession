package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.model.item.Inventory;
import java.util.List;
import org.junit.jupiter.api.Test;

class InventoryRowTest {

    @Test
    void listsTheItemsByNameWithQuantityAndDescription() {
        Inventory inventory = new Inventory();
        inventory.add(TestGames.NOTES, 2);
        inventory.add(TestGames.COFFEE, 1);

        List<InventoryRow> rows = InventoryRow.of(inventory);

        assertAll(
                () -> assertEquals(List.of(new InventoryRow(TestGames.NOTES, 2), new InventoryRow(TestGames.COFFEE, 1)),
                        rows),
                () -> assertEquals("Appunti × 2", rows.getFirst().label()),
                () -> assertEquals(TestGames.NOTES.description(), rows.getFirst().description()));
    }

    @Test
    void anEmptyInventoryHasNoRows() {
        assertEquals(List.of(), InventoryRow.of(new Inventory()));
    }

    @Test
    void rejectsInvalidRows() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new InventoryRow(null, 1)),
                () -> assertThrows(IllegalArgumentException.class, () -> new InventoryRow(TestGames.COFFEE, 0)));
    }
}
