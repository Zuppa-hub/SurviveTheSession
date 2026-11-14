package it.unicam.cs.mpgc.rpg127295.engine.repository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A saved game as plain data (DTO): only JDK types, no domain objects, so the file format
 * does not change when the domain classes do. {@link SaveMapper} converts it to and from the
 * domain. Values are not checked here: the domain checks them when the game is restored.
 * <p>
 * The remaining exams are not saved: they follow from the session and the transcript.
 *
 * @param studentName the name of the student
 * @param profileId   the id of the chosen profile
 * @param stats       the current stats
 * @param level       the current level
 * @param experience  the experience gained within the current level
 * @param inventory   item id to quantity; copied
 * @param transcript  exam code to grade (31 = 30 with honors), in recording order; copied
 */
public record SaveData(String studentName, String profileId, StatsData stats, int level, int experience,
                       Map<String, Integer> inventory, Map<String, Integer> transcript) {

    public SaveData {
        Objects.requireNonNull(studentName, "Student name must not be null");
        Objects.requireNonNull(profileId, "Profile id must not be null");
        Objects.requireNonNull(stats, "Stats must not be null");
        inventory = orderedCopy(Objects.requireNonNull(inventory, "Inventory must not be null"));
        transcript = orderedCopy(Objects.requireNonNull(transcript, "Transcript must not be null"));
    }

    private static Map<String, Integer> orderedCopy(Map<String, Integer> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    /** The stats of the student as plain numbers. */
    public record StatsData(int knowledge, int energy, int maxEnergy, int concentration, int maxConcentration) {
    }
}
