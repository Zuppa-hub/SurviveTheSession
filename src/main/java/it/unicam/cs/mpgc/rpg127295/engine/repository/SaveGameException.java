package it.unicam.cs.mpgc.rpg127295.engine.repository;

/**
 * A saved game cannot be read or written: corrupted file, unknown format version, data that
 * does not match the content, or an input/output error.
 */
public class SaveGameException extends RuntimeException {

    public SaveGameException(String message) {
        super(message);
    }

    public SaveGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
