package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveData;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Saves kept in memory, for the tests of the application wiring. */
final class InMemorySaveGameRepository implements SaveGameRepository {

    private final Map<String, SaveData> slots = new TreeMap<>();

    @Override
    public void save(String slot, SaveData data) {
        slots.put(SaveGameRepository.requireValidSlot(slot), data);
    }

    @Override
    public Optional<SaveData> load(String slot) {
        return Optional.ofNullable(slots.get(SaveGameRepository.requireValidSlot(slot)));
    }

    @Override
    public List<String> listSlots() {
        return List.copyOf(slots.keySet());
    }

    @Override
    public void delete(String slot) {
        slots.remove(SaveGameRepository.requireValidSlot(slot));
    }
}
