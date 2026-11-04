package it.unicam.cs.mpgc.rpg127295.model.student;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;

import it.unicam.cs.mpgc.rpg127295.model.item.Inventory;
import it.unicam.cs.mpgc.rpg127295.model.transcript.Transcript;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * The student played in a game: profile, statistics, level, inventory and transcript.
 * <p>
 * Statistics and level are immutable values replaced through
 * {@link #updateStats(UnaryOperator)} and {@link #updateLevel(UnaryOperator)}; the engine
 * decides how they change. Inventory and transcript are exposed directly because they protect
 * their own rules. No equals: a student has no stable identity (names are not unique).
 */
public final class Student {

    private final String name;
    private final StudentProfile profile;
    private final Inventory inventory = new Inventory();
    private final Transcript transcript = new Transcript();
    private Stats stats;
    private Level level;

    /**
     * Creates the student of a new game, with the profile's initial statistics, level 1, an
     * empty inventory and an empty transcript.
     *
     * @param name the player's name; surrounding whitespace is removed
     * @throws IllegalArgumentException if name is blank
     * @throws NullPointerException     if profile is null
     */
    public Student(String name, StudentProfile profile) {
        this.name = requireNonBlank(name, "Student name").strip();
        this.profile = Objects.requireNonNull(profile, "Profile must not be null");
        this.stats = profile.initialStats();
        this.level = Level.initial();
    }

    public String name() {
        return name;
    }

    public StudentProfile profile() {
        return profile;
    }

    public Stats stats() {
        return stats;
    }

    public Level level() {
        return level;
    }

    public Inventory inventory() {
        return inventory;
    }

    public Transcript transcript() {
        return transcript;
    }

    /**
     * Replaces the statistics with the result of a function applied to the current ones.
     * <p>
     * {@link UnaryOperator} is a functional interface, so the change is passed as a lambda,
     * for example {@code student.updateStats(stats -> stats.loseEnergy(3))}.
     *
     * @param change the function computing the new statistics; it must not return null
     * @throws NullPointerException if change is null or returns null; the statistics are
     *                              then left unchanged
     */
    public void updateStats(UnaryOperator<Stats> change) {
        Objects.requireNonNull(change, "Stats change must not be null");
        stats = Objects.requireNonNull(change.apply(stats), "Stats change must not return null");
    }

    /**
     * Replaces the level with the result of a function applied to the current one.
     * <p>
     * {@link UnaryOperator} is a functional interface, so the change is passed as a lambda,
     * for example {@code student.updateLevel(level -> level.addExperience(40))}.
     *
     * @param change the function computing the new level; it must not return null
     * @throws NullPointerException if change is null or returns null; the level is then left
     *                              unchanged
     */
    public void updateLevel(UnaryOperator<Level> change) {
        Objects.requireNonNull(change, "Level change must not be null");
        level = Objects.requireNonNull(change.apply(level), "Level change must not return null");
    }

    @Override
    public String toString() {
        return name + " (" + profile.name() + ", level " + level.number() + ")";
    }
}
