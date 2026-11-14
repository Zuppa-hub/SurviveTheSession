package it.unicam.cs.mpgc.rpg127295.persistence.content;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CatalogIndexTest {

    private record Entry(String key, String value) {
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry("zeta", "ultimo in ordine alfabetico"),
            new Entry("alfa", "primo in ordine alfabetico"),
            new Entry("mu", "in mezzo"));

    private static CatalogIndex<Entry> index(List<Entry> entries) {
        return CatalogIndex.of(entries, Entry::key, "entries.xml");
    }

    @Test
    void allKeepsTheContentOrder() {
        assertEquals(ENTRIES, index(ENTRIES).all());
    }

    @Test
    void findsEntriesByKey() {
        CatalogIndex<Entry> index = index(ENTRIES);

        assertAll(
                () -> assertEquals(Optional.of(ENTRIES.get(1)), index.find("alfa")),
                () -> assertEquals(Optional.empty(), index.find("omega")));
    }

    @Test
    void rejectsDuplicateKeys() {
        List<Entry> duplicated = List.of(new Entry("alfa", "uno"), new Entry("alfa", "due"));

        ContentException error = assertThrows(ContentException.class, () -> index(duplicated));

        assertAll(
                () -> assertTrue(error.getMessage().contains("entries.xml"), error.getMessage()),
                () -> assertTrue(error.getMessage().contains("alfa"), error.getMessage()));
    }

    @Test
    void allCannotBeModified() {
        assertThrows(UnsupportedOperationException.class, () -> index(ENTRIES).all().clear());
    }

    @Test
    void isNotAffectedByLaterChangesToTheSourceList() {
        List<Entry> source = new ArrayList<>(ENTRIES);
        CatalogIndex<Entry> index = index(source);

        source.add(new Entry("nuovo", "aggiunto dopo"));

        assertAll(
                () -> assertEquals(ENTRIES, index.all()),
                () -> assertEquals(Optional.empty(), index.find("nuovo")));
    }
}
