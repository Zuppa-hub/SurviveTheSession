package it.unicam.cs.mpgc.rpg127295.persistence.content;

import java.util.function.Supplier;

/**
 * Game content is missing or invalid.
 * <p>
 * Unchecked on purpose: there is nothing to recover at startup, so the composition root
 * catches it once and stops the program with its message (fail fast). The message always
 * names the content file and, when known, the position or the id of the faulty element.
 */
public class ContentException extends RuntimeException {

    public ContentException(String message) {
        super(message);
    }

    public ContentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Builds a domain object from content, turning a validation error of the domain into a
     * content error.
     *
     * @param location where the data comes from, such as "questions/prog.xml, PROG-01"
     * @throws ContentException if the domain rejects the data
     */
    public static <T> T building(String location, Supplier<T> builder) {
        try {
            return builder.get();
        } catch (IllegalArgumentException error) {
            throw new ContentException("Invalid content in " + location + ": " + error.getMessage(), error);
        }
    }
}
