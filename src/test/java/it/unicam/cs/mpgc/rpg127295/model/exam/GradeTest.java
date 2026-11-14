package it.unicam.cs.mpgc.rpg127295.model.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GradeTest {

    @ParameterizedTest
    @ValueSource(ints = {18, 24, 30, 31})
    void acceptsValuesFromEighteenToThirtyOne(int value) {
        assertEquals(value, Grade.of(value).value());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 17, 32, Integer.MAX_VALUE})
    void rejectsValuesOutsideTheRange(int value) {
        assertThrows(IllegalArgumentException.class, () -> Grade.of(value));
    }

    @Test
    void thirtyOneIsThirtyWithHonors() {
        Grade grade = Grade.of(31);

        assertTrue(grade.isWithHonors());
        assertEquals(30, grade.valueForAverage());
        assertEquals("30 e lode", grade.label());
    }

    @Test
    void ordinaryGradeHasNoHonors() {
        Grade grade = Grade.of(28);

        assertFalse(grade.isWithHonors());
        assertEquals(28, grade.valueForAverage());
        assertEquals("28", grade.label());
    }

    @Test
    void thirtyIsNotWithHonors() {
        assertFalse(Grade.of(30).isWithHonors());
        assertEquals("30", Grade.of(30).label());
    }

    @Test
    void toStringMatchesLabel() {
        assertEquals(Grade.of(31).label(), Grade.of(31).toString());
    }

    @Test
    void gradesWithSameValueAreEqual() {
        assertEquals(Grade.of(27), Grade.of(27));
        assertEquals(Grade.of(27).hashCode(), Grade.of(27).hashCode());
    }

    @Test
    void gradesWithDifferentValueAreNotEqual() {
        assertNotEquals(Grade.of(30), Grade.of(31));
    }

    @Test
    void gradesAreOrderedByValue() {
        assertTrue(Grade.of(18).compareTo(Grade.of(30)) < 0);
        assertTrue(Grade.of(31).compareTo(Grade.of(30)) > 0);
        assertEquals(0, Grade.of(25).compareTo(Grade.of(25)));
    }

    @Test
    void allGradesGoFromTheLowestToHonorsInOrder() {
        List<Grade> all = Grade.all();

        assertEquals(14, all.size());
        assertEquals(Grade.of(18), all.getFirst());
        assertEquals(Grade.of(31), all.getLast());
        assertEquals(all.stream().sorted().toList(), all);
    }
}
