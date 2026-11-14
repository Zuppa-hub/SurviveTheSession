package it.unicam.cs.mpgc.rpg127295.model;

/**
 * Shared argument checks for the domain classes.
 * <p>
 * Each method returns the checked value, so it can be used directly in an assignment, and
 * throws {@link IllegalArgumentException} with a message naming the argument. Null references
 * are checked with {@link java.util.Objects#requireNonNull(Object, String)} instead.
 */
public final class Preconditions {

    private Preconditions() {
    }

    /** @throws IllegalArgumentException if the value is null, empty or only whitespace */
    public static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    /** @throws IllegalArgumentException if the value is zero or negative */
    public static int requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive: " + value);
        }
        return value;
    }

    /** @throws IllegalArgumentException if the value is negative */
    public static int requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative: " + value);
        }
        return value;
    }
}
