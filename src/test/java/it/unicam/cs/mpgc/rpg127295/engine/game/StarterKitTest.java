package it.unicam.cs.mpgc.rpg127295.engine.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.model.item.Inventory;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StarterKitTest {

    private static final Item COFFEE = new Item("coffee", "Caffè", "Più tempo");
    private static final Item NOTES = new Item("notes", "Appunti", "Due opzioni in meno");

    @Test
    void givesEveryItemOfTheKit() {
        Inventory inventory = new Inventory();
        inventory.add(COFFEE, 1);

        new StarterKit(Map.of(COFFEE, 1, NOTES, 2)).giveTo(inventory);

        assertEquals(Map.of(COFFEE, 2, NOTES, 2), inventory.items());
    }

    @Test
    void anEmptyKitGivesNothing() {
        Inventory inventory = new Inventory();

        StarterKit.empty().giveTo(inventory);

        assertEquals(Map.of(), inventory.items());
    }

    @Test
    void theItemsAreCopied() {
        Map<Item, Integer> items = new HashMap<>(Map.of(COFFEE, 1));
        StarterKit kit = new StarterKit(items);

        items.put(NOTES, 1);

        assertAll(
                () -> assertEquals(Map.of(COFFEE, 1), kit.items()),
                () -> assertThrows(UnsupportedOperationException.class, () -> kit.items().put(NOTES, 1)));
    }

    @Test
    void rejectsInvalidKits() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new StarterKit(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StarterKit(Map.of(COFFEE, 0))),
                () -> assertThrows(NullPointerException.class, () -> StarterKit.empty().giveTo(null)));
    }
}
