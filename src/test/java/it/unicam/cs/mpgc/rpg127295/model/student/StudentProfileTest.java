package it.unicam.cs.mpgc.rpg127295.model.student;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StudentProfileTest {

    private static final Stats NERD_STATS = Stats.initial(8, 6, 5);

    private static StudentProfile nerd() {
        return new StudentProfile("nerd", "Secchione", "Studia sempre, dorme poco", NERD_STATS);
    }

    @Test
    void exposesItsData() {
        StudentProfile profile = nerd();

        assertAll(
                () -> assertEquals("nerd", profile.id()),
                () -> assertEquals("Secchione", profile.name()),
                () -> assertEquals("Studia sempre, dorme poco", profile.description()),
                () -> assertEquals(NERD_STATS, profile.initialStats()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankText(String blank) {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StudentProfile(blank, "Secchione", "Descrizione", NERD_STATS)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StudentProfile("nerd", blank, "Descrizione", NERD_STATS)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StudentProfile("nerd", "Secchione", blank, NERD_STATS)));
    }

    @Test
    void rejectsMissingStats() {
        assertThrows(NullPointerException.class,
                () -> new StudentProfile("nerd", "Secchione", "Descrizione", null));
    }

    @Test
    void rejectsInitialStatsThatAreNotFull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StudentProfile("nerd", "Secchione", "Descrizione", NERD_STATS.loseEnergy(1))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new StudentProfile("nerd", "Secchione", "Descrizione",
                                NERD_STATS.spendConcentration(1))));
    }

    @Test
    void profilesWithSameIdAreEqualEvenWithDifferentData() {
        StudentProfile other = new StudentProfile("nerd", "Altro nome", "Altra descrizione", Stats.initial(1, 1, 1));

        assertAll(
                () -> assertEquals(nerd(), other),
                () -> assertEquals(nerd().hashCode(), other.hashCode()));
    }

    @Test
    void profilesWithDifferentIdAreNotEqual() {
        assertNotEquals(nerd(), new StudentProfile("commuter", "Secchione", "Studia sempre, dorme poco", NERD_STATS));
    }
}
