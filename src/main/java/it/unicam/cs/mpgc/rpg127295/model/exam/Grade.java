package it.unicam.cs.mpgc.rpg127295.model.exam;

/**
 * An exam grade in the Italian university scale: from 18 to 30, where 31 stands for
 * "30 e lode" (30 with honors).
 * <p>
 * Immutable value object: two grades are equal when they have the same value.
 * Instances are created only through {@link #of(int)}, the single point where the range
 * is validated.
 */
public final class Grade implements Comparable<Grade> {

    private static final int MIN_VALUE = 18;
    private static final int MAX_ORDINARY_VALUE = 30;
    private static final int HONORS_VALUE = 31;

    private final int value;

    private Grade(int value) {
        this.value = value;
    }

    /**
     * Creates a grade.
     *
     * @param value a number from 18 to 30, or 31 for 30 with honors
     * @return the grade with the given value
     * @throws IllegalArgumentException if the value is outside 18-31
     */
    public static Grade of(int value) {
        if (value < MIN_VALUE || value > HONORS_VALUE) {
            throw new IllegalArgumentException(
                    "Grade must be between 18 and 31 (30 with honors): " + value);
        }
        return new Grade(value);
    }

    /** @return the raw value, from 18 to 31 */
    public int value() {
        return value;
    }

    public boolean isWithHonors() {
        return value == HONORS_VALUE;
    }

    /** @return the value used in averages, where honors counts as 30 */
    public int valueForAverage() {
        return Math.min(value, MAX_ORDINARY_VALUE);
    }

    /** @return the text shown to the player, such as "28" or "30 e lode" */
    public String label() {
        return isWithHonors() ? MAX_ORDINARY_VALUE + " e lode" : String.valueOf(value);
    }

    @Override
    public int compareTo(Grade other) {
        return Integer.compare(value, other.value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Grade grade && value == grade.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    /** Debug representation; use {@link #label()} for text shown to the player. */
    @Override
    public String toString() {
        return label();
    }
}
