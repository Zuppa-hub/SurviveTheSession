package it.unicam.cs.mpgc.rpg127295.model.question;

/**
 * A subject of the degree course, such as "Programmazione".
 * <p>
 * Subjects are data, not code: they are read from the content files, so adding a subject
 * requires no change to the program. Immutable value object.
 *
 * @param id   short unique identifier, such as "PROG"; never blank
 * @param name name shown to the player; never blank
 */
public record Subject(String id, String name) {

    /** @throws IllegalArgumentException if id or name is null or blank */
    public Subject {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Subject id must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Subject name must not be blank");
        }
    }
}
