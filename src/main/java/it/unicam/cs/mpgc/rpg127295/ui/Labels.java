package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** The Italian names of the enums shown on the screens, in one place. */
public final class Labels {

    private static final Map<ExamType, String> EXAM_TYPES = new EnumMap<>(Map.of(
            ExamType.WRITTEN, "Scritto",
            ExamType.ORAL, "Orale",
            ExamType.PROJECT, "Progetto"));

    private static final Map<Difficulty, String> DIFFICULTIES = new EnumMap<>(Map.of(
            Difficulty.EASY, "Facile",
            Difficulty.MEDIUM, "Media",
            Difficulty.HARD, "Difficile"));

    private Labels() {
    }

    /** @throws IllegalStateException if a new type of exam has no label yet */
    public static String of(ExamType type) {
        return labelOf(EXAM_TYPES, Objects.requireNonNull(type, "Exam type must not be null"));
    }

    /** @throws IllegalStateException if a new difficulty has no label yet */
    public static String of(Difficulty difficulty) {
        return labelOf(DIFFICULTIES, Objects.requireNonNull(difficulty, "Difficulty must not be null"));
    }

    private static <E extends Enum<E>> String labelOf(Map<E, String> labels, E value) {
        return Optional.ofNullable(labels.get(value))
                .orElseThrow(() -> new IllegalStateException("No label for " + value));
    }
}
