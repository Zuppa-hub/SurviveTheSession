package it.unicam.cs.mpgc.rpg127295.engine.game;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.List;
import java.util.Optional;

/** Catalogs for the tests, backed by a list. */
final class TestCatalogs {

    private TestCatalogs() {
    }

    static ItemCatalog items(Item... items) {
        List<Item> all = List.of(items);
        return new ItemCatalog() {
            @Override
            public List<Item> all() {
                return all;
            }

            @Override
            public Optional<Item> findById(String id) {
                return all.stream().filter(item -> item.id().equals(id)).findFirst();
            }
        };
    }
}
