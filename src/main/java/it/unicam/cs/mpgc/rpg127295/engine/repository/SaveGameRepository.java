package it.unicam.cs.mpgc.rpg127295.engine.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Where saved games are kept. Defined here, where it is used, and implemented in persistence.
 * <p>
 * Contract: a slot name has only letters, digits, {@code -} and {@code _} (otherwise
 * {@link IllegalArgumentException}), so it can never point outside the storage; reading or
 * writing problems throw {@link SaveGameException}.
 */
public interface SaveGameRepository {

    /** Saves the game in the slot, replacing what was there. */
    void save(String slot, SaveData data);

    /** @return the game in the slot, or empty if the slot has never been saved */
    Optional<SaveData> load(String slot);

    /** @return the saved slots, in alphabetical order */
    List<String> listSlots();

    /** Deletes the slot; nothing happens if it does not exist. */
    void delete(String slot);

    /**
     * @return the same slot name, if valid
     * @throws IllegalArgumentException if it contains anything but letters, digits, - and _
     */
    static String requireValidSlot(String slot) {
        Objects.requireNonNull(slot, "Slot must not be null");
        if (!slot.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid slot name, use only letters, digits, - and _: " + slot);
        }
        return slot;
    }
}
