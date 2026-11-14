package it.unicam.cs.mpgc.rpg127295.model.student;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;

import java.util.Objects;

/**
 * A student profile chosen at the start of a game, such as "Secchione", which determines the
 * initial statistics.
 * <p>
 * Immutable. Its identity is the {@code id}.
 */
public final class StudentProfile {

    private final String id;
    private final String name;
    private final String description;
    private final Stats initialStats;

    /**
     * @param initialStats the statistics of a new student, with energy and concentration at
     *                     their maximum
     * @throws IllegalArgumentException if a text is blank or the initial stats are not full
     * @throws NullPointerException     if initialStats is null
     */
    public StudentProfile(String id, String name, String description, Stats initialStats) {
        this.id = requireNonBlank(id, "Profile id");
        this.name = requireNonBlank(name, "Profile name");
        this.description = requireNonBlank(description, "Profile description");
        this.initialStats = requireFull(Objects.requireNonNull(initialStats, "Initial stats must not be null"));
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Stats initialStats() {
        return initialStats;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StudentProfile profile && id.equals(profile.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    private static Stats requireFull(Stats stats) {
        boolean full = stats.energy() == stats.maxEnergy() && stats.concentration() == stats.maxConcentration();
        if (!full) {
            throw new IllegalArgumentException("Initial stats must have full energy and concentration: " + stats);
        }
        return stats;
    }
}
