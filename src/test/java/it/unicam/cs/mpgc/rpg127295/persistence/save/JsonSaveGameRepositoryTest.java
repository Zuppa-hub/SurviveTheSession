package it.unicam.cs.mpgc.rpg127295.persistence.save;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveData;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonSaveGameRepositoryTest {

    private static final SaveData.StatsData STATS = new SaveData.StatsData(6, 4, 8, 3, 5);

    @TempDir
    Path directory;

    private JsonSaveGameRepository repository() {
        return new JsonSaveGameRepository(directory);
    }

    private static SaveData sample(String name) {
        Map<String, Integer> transcript = new LinkedHashMap<>();
        transcript.put("RETI1", 31);
        transcript.put("PROG1", 27);
        return new SaveData(name, "nerd", STATS, 2, 50, Map.of("coffee", 2), transcript);
    }

    private void writeFile(String slot, String content) throws IOException {
        Files.writeString(directory.resolve(slot + ".json"), content, StandardCharsets.UTF_8);
    }

    @Test
    void loadsWhatWasSaved() {
        JsonSaveGameRepository repository = repository();

        repository.save("andrea", sample("Andrea"));

        assertEquals(Optional.of(sample("Andrea")), repository.load("andrea"));
    }

    @Test
    void keepsTheTranscriptOrderAndNonAsciiText() {
        JsonSaveGameRepository repository = repository();

        repository.save("andrea", sample("Àndrea Cazzàto"));

        SaveData loaded = repository.load("andrea").orElseThrow();
        assertAll(
                () -> assertEquals("Àndrea Cazzàto", loaded.studentName()),
                () -> assertEquals(List.of("RETI1", "PROG1"), List.copyOf(loaded.transcript().keySet())));
    }

    @Test
    void writesTheFormatVersionInUtf8() throws IOException {
        repository().save("andrea", sample("Àndrea"));

        String json = Files.readString(directory.resolve("andrea.json"), StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(json.contains("\"formatVersion\": 1"), json),
                () -> assertTrue(json.contains("Àndrea"), json));
    }

    @Test
    void aMissingSlotIsEmpty() {
        assertEquals(Optional.empty(), repository().load("never-saved"));
    }

    @Test
    void savingAgainReplacesTheGameAndLeavesNoTemporaryFile() throws IOException {
        JsonSaveGameRepository repository = repository();
        repository.save("andrea", sample("Andrea"));

        repository.save("andrea", sample("Andrea Due"));

        try (Stream<Path> files = Files.list(directory)) {
            assertAll(
                    () -> assertEquals("Andrea Due", repository.load("andrea").orElseThrow().studentName()),
                    () -> assertEquals(List.of(directory.resolve("andrea.json")), files.toList()));
        }
    }

    @Test
    void createsTheDirectoryIfMissing() {
        JsonSaveGameRepository repository = new JsonSaveGameRepository(directory.resolve("saves/nested"));

        repository.save("andrea", sample("Andrea"));

        assertTrue(Files.exists(directory.resolve("saves/nested/andrea.json")));
    }

    @Test
    void listsTheSlotsInAlphabeticalOrder() throws IOException {
        JsonSaveGameRepository repository = repository();
        repository.save("zeta", sample("Z"));
        repository.save("alfa", sample("A"));
        Files.writeString(directory.resolve("notes.txt"), "not a save");

        assertAll(
                () -> assertEquals(List.of("alfa", "zeta"), repository.listSlots()),
                () -> assertEquals(List.of(), new JsonSaveGameRepository(directory.resolve("missing")).listSlots()));
    }

    @Test
    void deletesASlot() {
        JsonSaveGameRepository repository = repository();
        repository.save("andrea", sample("Andrea"));

        repository.delete("andrea");
        repository.delete("andrea");

        assertFalse(repository.load("andrea").isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{ not json", "[]", "{\"formatVersion\": 1}", "{\"game\": {}}",
            "{\"formatVersion\": 1, \"game\": {\"studentName\": \"Andrea\"}}"})
    void aCorruptedFileThrowsTheDedicatedException(String content) throws IOException {
        writeFile("broken", content);

        assertThrows(SaveGameException.class, () -> repository().load("broken"));
    }

    @Test
    void anUnknownFormatVersionThrowsTheDedicatedException() throws IOException {
        JsonSaveGameRepository repository = repository();
        repository.save("andrea", sample("Andrea"));
        String json = Files.readString(directory.resolve("andrea.json"));
        writeFile("future", json.replace("\"formatVersion\": 1", "\"formatVersion\": 2"));

        SaveGameException error = assertThrows(SaveGameException.class, () -> repository.load("future"));
        assertTrue(error.getMessage().contains("2"), error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"../outside", "a b", "slot.json", ""})
    void rejectsInvalidSlotNames(String slot) {
        JsonSaveGameRepository repository = repository();

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> repository.save(slot, sample("A"))),
                () -> assertThrows(IllegalArgumentException.class, () -> repository.load(slot)),
                () -> assertThrows(IllegalArgumentException.class, () -> repository.delete(slot)));
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new JsonSaveGameRepository(null)),
                () -> assertThrows(NullPointerException.class, () -> repository().save("andrea", null)));
    }
}
