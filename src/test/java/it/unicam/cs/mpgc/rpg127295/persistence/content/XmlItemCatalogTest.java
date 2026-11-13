package it.unicam.cs.mpgc.rpg127295.persistence.content;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class XmlItemCatalogTest {

    private static ItemCatalog load(String folder) {
        return XmlItemCatalog.load(new XmlDocumentLoader("/content-test/items/" + folder + "/", "/content/schema/"));
    }

    @Test
    void loadsTheItemsInFileOrder() {
        List<Item> items = load("valid").all();

        assertAll(
                () -> assertEquals(List.of("coffee", "energy-drink"), items.stream().map(Item::id).toList()),
                () -> assertEquals("Caffè", items.getFirst().name()),
                () -> assertEquals("+15 secondi alla domanda corrente", items.getFirst().description()));
    }

    @Test
    void findsItemsById() {
        ItemCatalog catalog = load("valid");

        assertAll(
                () -> assertEquals("Energy drink", catalog.findById("energy-drink").orElseThrow().name()),
                () -> assertEquals(Optional.empty(), catalog.findById("pizza")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"duplicate-id", "blank-name", "missing-description"})
    void rejectsInvalidFiles(String folder) {
        ContentException error = assertThrows(ContentException.class, () -> load(folder));

        assertTrue(error.getMessage().contains("items.xml"), error.getMessage());
    }
}
