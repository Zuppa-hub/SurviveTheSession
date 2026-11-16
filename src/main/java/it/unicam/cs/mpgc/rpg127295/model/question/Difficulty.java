package it.unicam.cs.mpgc.rpg127295.model.question;

import java.util.Objects;

/**
 * Difficulty of a question or of an exam.
 * <p>
 * It carries no game numbers (damage, points): those are balancing rules owned by the engine.
 */
public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    /** @return the next difficulty level; {@link #HARD} stays {@link #HARD} */
    public Difficulty harder() {
        return switch (this) {
            case EASY -> MEDIUM;
            case MEDIUM, HARD -> HARD;
        };
    }

    /** @return the steps between this difficulty and the other: 0 for the same, 2 from easy to hard */
    public int distanceFrom(Difficulty other) {
        Objects.requireNonNull(other, "Difficulty must not be null");
        return Math.abs(ordinal() - other.ordinal());
    }
}
