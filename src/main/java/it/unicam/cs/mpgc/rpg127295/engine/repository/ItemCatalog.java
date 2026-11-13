package it.unicam.cs.mpgc.rpg127295.engine.repository;

import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.List;
import java.util.Optional;

/** The items that exist in the game. Defined here, where it is used, and implemented in persistence. */
public interface ItemCatalog {

    /** @return every item, in content order; unmodifiable */
    List<Item> all();

    /** @return the item with the given id, or empty if none exists */
    Optional<Item> findById(String id);
}
