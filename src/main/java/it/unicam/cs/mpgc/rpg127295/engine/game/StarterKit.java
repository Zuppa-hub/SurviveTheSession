package it.unicam.cs.mpgc.rpg127295.engine.game;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

import it.unicam.cs.mpgc.rpg127295.model.item.Inventory;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.Map;
import java.util.Objects;

/**
 * The items every new student receives, so that the items and their effects are part of the
 * game from the first exam. It is given only when a game is created: a loaded game keeps the
 * inventory it was saved with.
 *
 * @param items item to quantity, every quantity positive; copied
 */
public record StarterKit(Map<Item, Integer> items) {

    /**
     * @throws NullPointerException     if the items are null
     * @throws IllegalArgumentException if a quantity is not positive
     */
    public StarterKit {
        items = Map.copyOf(Objects.requireNonNull(items, "Items must not be null"));
        items.values().forEach(quantity -> requirePositive(quantity, "Quantity"));
    }

    public static StarterKit empty() {
        return new StarterKit(Map.of());
    }

    public void giveTo(Inventory inventory) {
        Objects.requireNonNull(inventory, "Inventory must not be null");
        items.forEach(inventory::add);
    }
}
