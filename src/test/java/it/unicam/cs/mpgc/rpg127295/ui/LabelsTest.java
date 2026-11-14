package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class LabelsTest {

    @Test
    void namesTypesAndDifficultiesInItalian() {
        assertAll(
                () -> assertEquals("Scritto", Labels.of(ExamType.WRITTEN)),
                () -> assertEquals("Orale", Labels.of(ExamType.ORAL)),
                () -> assertEquals("Progetto", Labels.of(ExamType.PROJECT)),
                () -> assertEquals("Facile", Labels.of(Difficulty.EASY)),
                () -> assertEquals("Media", Labels.of(Difficulty.MEDIUM)),
                () -> assertEquals("Difficile", Labels.of(Difficulty.HARD)));
    }

    @ParameterizedTest
    @EnumSource(ExamType.class)
    void everyExamTypeHasALabel(ExamType type) {
        assertFalse(Labels.of(type).isBlank());
    }

    @ParameterizedTest
    @EnumSource(Difficulty.class)
    void everyDifficultyHasALabel(Difficulty difficulty) {
        assertFalse(Labels.of(difficulty).isBlank());
    }

    @Test
    void rejectsNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> Labels.of((ExamType) null)),
                () -> assertThrows(NullPointerException.class, () -> Labels.of((Difficulty) null)));
    }
}
