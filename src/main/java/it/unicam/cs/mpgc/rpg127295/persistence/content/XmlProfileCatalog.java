package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ProfileCatalog;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Optional;
import org.w3c.dom.Element;

/**
 * The student profiles read from {@code profiles.xml}, validated by {@code profiles.xsd}.
 * Immutable. The energy and concentration attributes are the maximum values, which a new
 * student starts with.
 */
public final class XmlProfileCatalog implements ProfileCatalog {

    private static final String FILE = "profiles.xml";
    private static final String SCHEMA = "profiles.xsd";

    private final List<StudentProfile> profiles;

    private XmlProfileCatalog(List<StudentProfile> profiles) {
        this.profiles = List.copyOf(profiles);
    }

    /** @throws ContentException if the file is missing or invalid */
    public static XmlProfileCatalog load(XmlDocumentLoader loader) {
        XmlReader reader = new XmlReader();
        Element root = loader.load(FILE, SCHEMA).getDocumentElement();
        return new XmlProfileCatalog(reader.elements(root, "profile").stream()
                .map(element -> toProfile(element, reader))
                .toList());
    }

    @Override
    public List<StudentProfile> all() {
        return profiles;
    }

    @Override
    public Optional<StudentProfile> findById(String id) {
        return profiles.stream()
                .filter(profile -> profile.id().equals(id))
                .findFirst();
    }

    private static StudentProfile toProfile(Element element, XmlReader reader) {
        String id = element.getAttribute("id");
        return ContentException.building(FILE + ", profile " + id, () -> new StudentProfile(
                id,
                reader.text(element, "name"),
                reader.text(element, "description"),
                initialStats(element)));
    }

    private static Stats initialStats(Element element) {
        return Stats.initial(
                Integer.parseInt(element.getAttribute("knowledge")),
                Integer.parseInt(element.getAttribute("energy")),
                Integer.parseInt(element.getAttribute("concentration")));
    }
}
