package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SubjectTest {

    @Test
    void exposesIdAndName() {
        Subject subject = new Subject("PROG", "Programmazione");

        assertEquals("PROG", subject.id());
        assertEquals("Programmazione", subject.name());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankId(String id) {
        assertThrows(IllegalArgumentException.class, () -> new Subject(id, "Programmazione"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Subject("PROG", name));
    }

    @Test
    void subjectsWithSameFieldsAreEqual() {
        assertEquals(new Subject("PROG", "Programmazione"), new Subject("PROG", "Programmazione"));
        assertEquals(new Subject("PROG", "Programmazione").hashCode(),
                new Subject("PROG", "Programmazione").hashCode());
    }

    @Test
    void subjectsWithDifferentIdAreNotEqual() {
        assertNotEquals(new Subject("PROG", "Programmazione"), new Subject("ASD", "Programmazione"));
    }
}
