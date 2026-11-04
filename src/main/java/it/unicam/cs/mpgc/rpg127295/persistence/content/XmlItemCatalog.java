package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.List;
import java.util.Optional;
import org.w3c.dom.Element;

/** The items read from {@code items.xml}, validated by {@code items.xsd}. Immutable. */
public final class XmlItemCatalog implements ItemCatalog {

    private static final String FILE = "items.xml";
    private static final String SCHEMA = "items.xsd";

    private final List<Item> items;

    private XmlItemCatalog(List<Item> items) {
        this.items = List.copyOf(items);
    }

    /** @throws ContentException if the file is missing or invalid */
    public static XmlItemCatalog load(XmlDocumentLoader loader) {
        XmlReader reader = new XmlReader();
        Element root = loader.load(FILE, SCHEMA).getDocumentElement();
        return new XmlItemCatalog(reader.elements(root, "item").stream()
                .map(element -> toItem(element, reader))
                .toList());
    }

    @Override
    public List<Item> all() {
        return items;
    }

    @Override
    public Optional<Item> findById(String id) {
        return items.stream()
                .filter(item -> item.id().equals(id))
                .findFirst();
    }

    private static Item toItem(Element element, XmlReader reader) {
        String id = element.getAttribute("id");
        return ContentException.building(FILE + ", item " + id,
                () -> new Item(id, reader.text(element, "name"), reader.text(element, "description")));
    }
}
