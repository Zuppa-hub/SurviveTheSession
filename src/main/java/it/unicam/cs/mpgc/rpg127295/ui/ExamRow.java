package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * One exam of the session as shown on the session screen: passed with its grade, available, or
 * locked (the final boss). Computed from the game, so the screen holds no game rule.
 *
 * @param exam   the exam
 * @param status whether it can be taken
 * @param grade  the grade, present if and only if the exam is passed
 */
public record ExamRow(Exam exam, Status status, Optional<Grade> grade) {

    private static final Map<ExamType, String> TYPE_LABELS = new EnumMap<>(Map.of(
            ExamType.WRITTEN, "Scritto",
            ExamType.ORAL, "Orale",
            ExamType.PROJECT, "Progetto"));

    /** Whether an exam can be taken now. */
    public enum Status {
        PASSED,
        AVAILABLE,
        LOCKED
    }

    public ExamRow {
        Objects.requireNonNull(exam, "Exam must not be null");
        Objects.requireNonNull(status, "Status must not be null");
        Objects.requireNonNull(grade, "Grade must not be null");
        if ((status == Status.PASSED) != grade.isPresent()) {
            throw new IllegalArgumentException("A grade is required if and only if the exam is passed");
        }
    }

    /** @return a row for every exam of the session, in session order */
    public static List<ExamRow> of(Game game) {
        Objects.requireNonNull(game, "Game must not be null");
        List<Exam> available = game.availableExams();
        return game.session().exams().stream()
                .map(exam -> rowOf(game, available, exam))
                .toList();
    }

    private static ExamRow rowOf(Game game, List<Exam> available, Exam exam) {
        Optional<Grade> grade = game.student().transcript().gradeOf(exam);
        if (grade.isPresent()) {
            return new ExamRow(exam, Status.PASSED, grade);
        }
        return new ExamRow(exam, available.contains(exam) ? Status.AVAILABLE : Status.LOCKED, Optional.empty());
    }

    public String typeLabel() {
        return TYPE_LABELS.get(exam.type());
    }

    public String statusLabel() {
        return switch (status) {
            case PASSED -> "Superato con " + grade.orElseThrow().label();
            case AVAILABLE -> "Da sostenere";
            case LOCKED -> "Boss finale: dopo tutti gli altri esami";
        };
    }
}
