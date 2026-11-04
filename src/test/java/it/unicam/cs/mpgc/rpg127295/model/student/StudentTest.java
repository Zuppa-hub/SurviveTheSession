package it.unicam.cs.mpgc.rpg127295.model.student;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StudentTest {

    private static final Stats NERD_STATS = Stats.initial(8, 6, 5);
    private static final StudentProfile NERD = new StudentProfile("nerd", "Secchione", "Studia sempre", NERD_STATS);

    private static Student newStudent() {
        return new Student("Andrea", NERD);
    }

    @Test
    void newStudentStartsFromTheProfile() {
        Student student = newStudent();

        assertAll(
                () -> assertEquals("Andrea", student.name()),
                () -> assertEquals(NERD, student.profile()),
                () -> assertEquals(NERD_STATS, student.stats()),
                () -> assertEquals(Level.initial(), student.level()),
                () -> assertTrue(student.inventory().isEmpty()),
                () -> assertEquals(List.of(), student.transcript().entries()));
    }

    @Test
    void nameIsStripped() {
        assertEquals("Andrea", new Student("  Andrea \t", NERD).name());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Student(name, NERD));
    }

    @Test
    void rejectsMissingProfile() {
        assertThrows(NullPointerException.class, () -> new Student("Andrea", null));
    }

    @Test
    void updateStatsAppliesTheGivenFunction() {
        Student student = newStudent();

        student.updateStats(stats -> stats.loseEnergy(2));

        assertEquals(NERD_STATS.loseEnergy(2), student.stats());
    }

    @Test
    void updateLevelAppliesTheGivenFunction() {
        Student student = newStudent();

        student.updateLevel(level -> level.addExperience(40));

        assertEquals(new Level(1, 40), student.level());
    }

    @Test
    void rejectsMissingOrNullReturningFunctions() {
        Student student = newStudent();

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> student.updateStats(null)),
                () -> assertThrows(NullPointerException.class, () -> student.updateStats(stats -> null)),
                () -> assertThrows(NullPointerException.class, () -> student.updateLevel(null)),
                () -> assertThrows(NullPointerException.class, () -> student.updateLevel(level -> null)),
                () -> assertEquals(NERD_STATS, student.stats()),
                () -> assertEquals(Level.initial(), student.level()));
    }

    @Test
    void inventoryAndTranscriptKeepTheirChanges() {
        Student student = newStudent();
        Item coffee = new Item("coffee", "Caffè", "+15 secondi");
        Exam programming = new Exam(
                "PROG1", new Subject("PROG", "Programmazione"), ExamType.WRITTEN, 12, Difficulty.MEDIUM);

        student.inventory().add(coffee, 1);
        student.transcript().record(programming, Grade.of(28));

        assertAll(
                () -> assertEquals(1, student.inventory().quantityOf(coffee)),
                () -> assertTrue(student.transcript().hasPassed(programming)));
    }

    @Test
    void studentsDoNotShareInventoryOrTranscript() {
        Student first = newStudent();
        Student second = newStudent();

        assertAll(
                () -> assertNotSame(first.inventory(), second.inventory()),
                () -> assertNotSame(first.transcript(), second.transcript()));
    }
}
