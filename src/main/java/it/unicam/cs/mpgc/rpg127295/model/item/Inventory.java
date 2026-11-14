package it.unicam.cs.mpgc.rpg127295.model.item;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The items owned by the student, each with its quantity.
 * <p>
 * Invariant: every stored quantity is positive; an item whose quantity drops to zero is no
 * longer in the inventory.
 */
public final class Inventory {

    private final Map<Item, Integer> quantities = new HashMap<>();

    /**
     * @param quantity how many units to add; always positive
     * @throws NullPointerException     if item is null
     * @throws IllegalArgumentException if quantity is not positive
     */
    public void add(Item item, int quantity) {
        Objects.requireNonNull(item, "Item must not be null");
        requirePositive(quantity, "Quantity");
        quantities.merge(item, quantity, Integer::sum);
    }

    /**
     * Removes one unit of the item.
     *
     * @throws NullPointerException  if item is null
     * @throws IllegalStateException if the item is not in the inventory; check
     *                               {@link #contains(Item)} first
     */
    public void remove(Item item) {
        Objects.requireNonNull(item, "Item must not be null");
        if (!contains(item)) {
            throw new IllegalStateException("Item not in the inventory: " + item.id());
        }
        int remaining = quantities.get(item) - 1;
        if (remaining == 0) {
            quantities.remove(item);
        } else {
            quantities.put(item, remaining);
        }
    }

    /** @return the quantity of the item, 0 if absent */
    public int quantityOf(Item item) {
        return quantities.getOrDefault(item, 0);
    }

    public boolean contains(Item item) {
        return quantities.containsKey(item);
    }

    public boolean isEmpty() {
        return quantities.isEmpty();
    }

    /** @return an unmodifiable snapshot of the items and their quantities */
    public Map<Item, Integer> items() {
        return Map.copyOf(quantities);
    }
}
