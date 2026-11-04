package it.unicam.cs.mpgc.rpg127295.persistence.content;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * The objects of a catalog, indexed by key when the content is loaded. Immutable.
 * <p>
 * Why a map: a lookup by key is O(1) on a {@link Map}, while scanning a {@link List} with a
 * stream is O(n) (lesson 14). Why a {@link LinkedHashMap}: it keeps insertion order, so
 * {@link #all()} still returns the objects in content order. Shared by the XML catalogs
 * through composition; only the way the key is taken from an object changes between them.
 *
 * @param <T> the type of the catalog objects
 */
final class CatalogIndex<T> {

    private final Map<String, T> byKey;
    private final List<T> all;

    private CatalogIndex(Map<String, T> byKey) {
        this.byKey = Collections.unmodifiableMap(byKey);
        this.all = List.copyOf(byKey.values());
    }

    /**
     * @param items the objects in content order
     * @param keyOf extracts the key of an object, such as {@code Item::id}
     * @param file  the content file, named in error messages
     * @throws ContentException if two objects have the same key; the schemas already prevent
     *                          it, this is a second line of defence
     */
    static <T> CatalogIndex<T> of(List<T> items, Function<T, String> keyOf, String file) {
        Map<String, T> byKey = new LinkedHashMap<>();
        for (T item : items) {
            String key = keyOf.apply(item);
            if (byKey.putIfAbsent(key, item) != null) {
                throw new ContentException("Invalid content in " + file + ": duplicate key " + key);
            }
        }
        return new CatalogIndex<>(byKey);
    }

    /** @return every object in content order; unmodifiable */
    List<T> all() {
        return all;
    }

    /** @return the object with the given key, or empty if none exists */
    Optional<T> find(String key) {
        return Optional.ofNullable(byKey.get(key));
    }
}
