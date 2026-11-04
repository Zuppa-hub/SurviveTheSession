package it.unicam.cs.mpgc.rpg127295.model.question;

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
}
