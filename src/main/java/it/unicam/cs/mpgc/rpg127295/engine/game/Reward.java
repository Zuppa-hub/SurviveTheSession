package it.unicam.cs.mpgc.rpg127295.engine.game;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonNegative;
import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.Map;
import java.util.Objects;

/**
 * What the student receives for passing an exam.
 *
 * @param experience the experience gained; not negative
 * @param items      the items received with their quantity, each positive; copied
 */
public record Reward(int experience, Map<Item, Integer> items) {

    public Reward {
        requireNonNegative(experience, "Experience");
        items = Map.copyOf(Objects.requireNonNull(items, "Items must not be null"));
        items.values().forEach(quantity -> requirePositive(quantity, "Item quantity"));
    }

    /** @return no experience and no items, for an exam not passed */
    public static Reward none() {
        return new Reward(0, Map.of());
    }
}
