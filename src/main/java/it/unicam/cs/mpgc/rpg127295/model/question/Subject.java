package it.unicam.cs.mpgc.rpg127295.model.question;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;

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
        requireNonBlank(id, "Subject id");
        requireNonBlank(name, "Subject name");
    }
}
