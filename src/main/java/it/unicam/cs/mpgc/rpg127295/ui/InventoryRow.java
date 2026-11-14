package it.unicam.cs.mpgc.rpg127295.ui;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

import it.unicam.cs.mpgc.rpg127295.model.item.Inventory;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** An item of the inventory as shown in the session: name and quantity, description as tooltip. */
public record InventoryRow(Item item, int quantity) {

    public InventoryRow {
        Objects.requireNonNull(item, "Item must not be null");
        requirePositive(quantity, "Quantity");
    }

    /** @return the items owned, in order of name */
    public static List<InventoryRow> of(Inventory inventory) {
        return Objects.requireNonNull(inventory, "Inventory must not be null").items().entrySet().stream()
                .map(entry -> new InventoryRow(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(row -> row.item().name()))
                .toList();
    }

    public String label() {
        return item.name() + " × " + quantity;
    }

    public String description() {
        return item.description();
    }
}
