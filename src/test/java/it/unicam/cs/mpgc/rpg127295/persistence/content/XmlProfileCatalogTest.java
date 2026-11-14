package it.unicam.cs.mpgc.rpg127295.persistence.content;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ProfileCatalog;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class XmlProfileCatalogTest {

    private static ProfileCatalog load(String folder) {
        return XmlProfileCatalog.load(
                new XmlDocumentLoader("/content-test/profiles/" + folder + "/", "/content/schema/"));
    }

    @Test
    void loadsTheProfilesInFileOrder() {
        List<StudentProfile> profiles = load("valid").all();
        StudentProfile nerd = profiles.getFirst();

        assertAll(
                () -> assertEquals(List.of("nerd", "commuter"), profiles.stream().map(StudentProfile::id).toList()),
                () -> assertEquals("Secchione", nerd.name()),
                () -> assertEquals("Sa tutto, ma si stanca presto", nerd.description()),
                () -> assertEquals(Stats.initial(8, 6, 4), nerd.initialStats()));
    }

    @Test
    void findsProfilesById() {
        ProfileCatalog catalog = load("valid");

        assertAll(
                () -> assertEquals("Pendolare", catalog.findById("commuter").orElseThrow().name()),
                () -> assertEquals(Optional.empty(), catalog.findById("night-owl")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"duplicate-id", "zero-energy", "missing-knowledge", "negative-concentration"})
    void rejectsInvalidFiles(String folder) {
        ContentException error = assertThrows(ContentException.class, () -> load(folder));

        assertTrue(error.getMessage().contains("profiles.xml"), error.getMessage());
    }
}
