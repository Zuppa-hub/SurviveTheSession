package it.unicam.cs.mpgc.rpg127295.engine.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class SaveGameExceptionTest {

    @Test
    void keepsTheMessageAndTheCause() {
        IOException cause = new IOException("disk full");
        SaveGameException exception = new SaveGameException("Cannot save slot andrea", cause);

        assertAll(
                () -> assertEquals("Cannot save slot andrea", exception.getMessage()),
                () -> assertSame(cause, exception.getCause()));
    }
}
