package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

/** The texts shown on more than one screen, written once. */
class StatTextsTest {

    private static final Stats STATS = Stats.initial(8, 7, 4).loseEnergy(3);

    @Test
    void describesTheStatsAsValueOverMaximum() {
        assertAll(
                () -> assertEquals("Energia 4 / 7", StatTexts.energy(STATS)),
                () -> assertEquals("Concentrazione 4 / 4", StatTexts.concentration(STATS)),
                () -> assertEquals("Resistenza dell'esame 44 / 60", StatTexts.resistance(44, 60)));
    }

    @Test
    void theShareFillsAProgressBar() {
        assertAll(
                () -> assertEquals(0.5, StatTexts.share(3, 6)),
                () -> assertEquals(0.0, StatTexts.share(0, 6)),
                () -> assertThrows(IllegalArgumentException.class, () -> StatTexts.share(1, 0)));
    }

    @Test
    void theAverageHasTwoDecimalsInItalian() {
        assertAll(
                () -> assertEquals("26,47", StatTexts.average(26.4705)),
                () -> assertEquals("Media ponderata 26,47", StatTexts.average(OptionalDouble.of(26.4705))),
                () -> assertEquals("Nessun esame superato", StatTexts.average(OptionalDouble.empty())));
    }

    @Test
    void creditsAndGraduationGrade() {
        assertAll(
                () -> assertEquals("51 CFU", StatTexts.credits(51)),
                () -> assertEquals("Voto di laurea 97/110", StatTexts.graduationGrade(97)));
    }
}
