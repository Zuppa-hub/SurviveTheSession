package it.unicam.cs.mpgc.rpg127295.engine.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RewardTest {

    private static final Item COFFEE = new Item("coffee", "Caffè", "Più tempo");

    @Test
    void noneGivesNothing() {
        assertAll(
                () -> assertEquals(0, Reward.none().experience()),
                () -> assertTrue(Reward.none().items().isEmpty()));
    }

    @Test
    void isNotAffectedByChangesToTheOriginalMap() {
        Map<Item, Integer> items = new HashMap<>(Map.of(COFFEE, 1));
        Reward reward = new Reward(10, items);

        items.put(COFFEE, 5);

        assertAll(
                () -> assertEquals(Map.of(COFFEE, 1), reward.items()),
                () -> assertThrows(UnsupportedOperationException.class, () -> reward.items().clear()));
    }

    @Test
    void rejectsInvalidValues() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Reward(-1, Map.of())),
                () -> assertThrows(IllegalArgumentException.class, () -> new Reward(0, Map.of(COFFEE, 0))),
                () -> assertThrows(NullPointerException.class, () -> new Reward(0, null)));
    }
}
