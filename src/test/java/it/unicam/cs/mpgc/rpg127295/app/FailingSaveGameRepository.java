package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveData;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameRepository;
import java.util.List;
import java.util.Optional;

/** A repository whose every save fails, as with a full disk. */
final class FailingSaveGameRepository implements SaveGameRepository {

    private final SaveGameException failure;

    FailingSaveGameRepository(SaveGameException failure) {
        this.failure = failure;
    }

    @Override
    public void save(String slot, SaveData data) {
        throw failure;
    }

    @Override
    public Optional<SaveData> load(String slot) {
        return Optional.empty();
    }

    @Override
    public List<String> listSlots() {
        return List.of();
    }

    @Override
    public void delete(String slot) {
    }
}
