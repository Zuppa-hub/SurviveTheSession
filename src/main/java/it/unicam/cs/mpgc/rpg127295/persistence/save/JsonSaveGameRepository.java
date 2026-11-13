package it.unicam.cs.mpgc.rpg127295.persistence.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveData;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameRepository;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Saved games as JSON files, one per slot: {@code <directory>/<slot>.json}.
 * <ul>
 *   <li>The file holds a {@code formatVersion} next to the game: a version this code does not
 *       know is refused instead of being read wrongly.</li>
 *   <li>A game is written to a temporary file in the same directory and then moved over the
 *       old one, atomically where the file system allows it: an interruption never leaves a
 *       half-written save.</li>
 *   <li>Files are read and written with buffered streams in UTF-8.</li>
 * </ul>
 */
public final class JsonSaveGameRepository implements SaveGameRepository {

    private static final int FORMAT_VERSION = 1;
    private static final String VERSION_FIELD = "formatVersion";
    private static final String GAME_FIELD = "game";
    private static final String EXTENSION = ".json";

    private final Path directory;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** @param directory where the saves are kept; created at the first save if missing */
    public JsonSaveGameRepository(Path directory) {
        this.directory = Objects.requireNonNull(directory, "Directory must not be null");
    }

    @Override
    public void save(String slot, SaveData data) {
        Path target = fileOf(slot);
        Objects.requireNonNull(data, "Save data must not be null");
        try {
            Files.createDirectories(directory);
            Path temporary = Files.createTempFile(directory, slot + "-", ".tmp");
            try {
                write(temporary, data);
                replace(temporary, target);
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch (IOException error) {
            throw new SaveGameException("Cannot save slot " + slot, error);
        }
    }

    @Override
    public Optional<SaveData> load(String slot) {
        Path file = fileOf(slot);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        JsonObject root = parse(file);
        requireKnownVersion(root, slot);
        return Optional.of(gameOf(root, slot));
    }

    @Override
    public List<String> listSlots() {
        if (!Files.isDirectory(directory)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(directory)) {
            return files.map(file -> file.getFileName().toString())
                    .filter(name -> name.endsWith(EXTENSION))
                    .map(name -> name.substring(0, name.length() - EXTENSION.length()))
                    .filter(JsonSaveGameRepository::isValidSlot)
                    .sorted()
                    .toList();
        } catch (IOException error) {
            throw new SaveGameException("Cannot list the saved games", error);
        }
    }

    @Override
    public void delete(String slot) {
        try {
            Files.deleteIfExists(fileOf(slot));
        } catch (IOException error) {
            throw new SaveGameException("Cannot delete slot " + slot, error);
        }
    }

    private Path fileOf(String slot) {
        return directory.resolve(SaveGameRepository.requireValidSlot(slot) + EXTENSION);
    }

    private static boolean isValidSlot(String name) {
        try {
            SaveGameRepository.requireValidSlot(name);
            return true;
        } catch (IllegalArgumentException invalid) {
            return false;
        }
    }

    private void write(Path file, SaveData data) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(new SaveFile(FORMAT_VERSION, data), writer);
        }
    }

    private static void replace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException notSupported) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static JsonObject parse(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) {
                throw new SaveGameException("Not a saved game: " + file.getFileName());
            }
            return root.getAsJsonObject();
        } catch (IOException | JsonParseException error) {
            throw new SaveGameException("Cannot read saved game " + file.getFileName(), error);
        }
    }

    private static void requireKnownVersion(JsonObject root, String slot) {
        JsonElement version = root.get(VERSION_FIELD);
        if (version == null || !version.isJsonPrimitive() || !version.getAsJsonPrimitive().isNumber()) {
            throw new SaveGameException("Saved game " + slot + " has no format version");
        }
        if (version.getAsInt() != FORMAT_VERSION) {
            throw new SaveGameException("Saved game " + slot + " has unknown format version " + version.getAsString());
        }
    }

    /**
     * Any failure while building the game from the file means the file is corrupted: Gson
     * reports a missing or wrong field with an exception from the record constructor.
     */
    private SaveData gameOf(JsonObject root, String slot) {
        SaveData data;
        try {
            data = gson.fromJson(root.get(GAME_FIELD), SaveData.class);
        } catch (RuntimeException corrupted) {
            throw new SaveGameException("Saved game " + slot + " is corrupted", corrupted);
        }
        if (data == null) {
            throw new SaveGameException("Saved game " + slot + " contains no game");
        }
        return data;
    }

    /** The content of a file: the format version and the game. */
    private record SaveFile(int formatVersion, SaveData game) {
    }
}
