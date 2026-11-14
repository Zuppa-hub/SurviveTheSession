package it.unicam.cs.mpgc.rpg127295.model.item;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;

/**
 * An item that the student can own, such as a coffee or a summary.
 * <p>
 * Only data: what an item does during an exam is defined in the engine, so that the model
 * depends on nothing. Immutable. Its identity is the {@code id}: two items with the same id
 * are the same item for the inventory.
 */
public final class Item {

    private final String id;
    private final String name;
    private final String description;

    /** @throws IllegalArgumentException if a field is null or blank */
    public Item(String id, String name, String description) {
        this.id = requireNonBlank(id, "Item id");
        this.name = requireNonBlank(name, "Item name");
        this.description = requireNonBlank(description, "Item description");
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Item item && id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
