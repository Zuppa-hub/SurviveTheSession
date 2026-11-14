package it.unicam.cs.mpgc.rpg127295.model.transcript;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * The student's transcript ("libretto"): the passed exams with their grades.
 * <p>
 * Single responsibility: it records exams and computes statistics on them (total credits,
 * weighted average). Graduation rules and the final degree grade do not belong here: they
 * are game rules and live in the engine.
 */
public final class Transcript {

    private final Map<Exam, Grade> grades = new LinkedHashMap<>();

    /**
     * Records a passed exam.
     * <p>
     * A failing grade cannot be recorded: {@link Grade} cannot exist below 18, so the invalid
     * state is prevented by the type, not by a check here.
     *
     * @throws NullPointerException  if exam or grade is null
     * @throws IllegalStateException if the exam is already recorded; check
     *                               {@link #hasPassed(Exam)} first
     */
    public void record(Exam exam, Grade grade) {
        Objects.requireNonNull(exam, "Exam must not be null");
        Objects.requireNonNull(grade, "Grade must not be null");
        if (hasPassed(exam)) {
            throw new IllegalStateException("Exam already recorded: " + exam.code());
        }
        grades.put(exam, grade);
    }

    public boolean hasPassed(Exam exam) {
        return grades.containsKey(exam);
    }

    /** @return the grade of the exam, or empty if it has not been passed */
    public Optional<Grade> gradeOf(Exam exam) {
        return Optional.ofNullable(grades.get(exam));
    }

    /** @return the entries in recording order; an unmodifiable snapshot */
    public List<TranscriptEntry> entries() {
        return grades.entrySet().stream()
                .map(entry -> new TranscriptEntry(entry.getKey(), entry.getValue()))
                .toList();
    }

    /** @return the sum of the credits of the passed exams */
    public int totalCredits() {
        return grades.keySet().stream()
                .mapToInt(Exam::credits)
                .sum();
    }

    /**
     * Average of the grades weighted by credits; honors counts as 30.
     *
     * @return the average, or empty if no exam has been passed
     */
    public OptionalDouble weightedAverage() {
        int credits = totalCredits();
        if (credits == 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of((double) weightedGradeSum() / credits);
    }

    private int weightedGradeSum() {
        return grades.entrySet().stream()
                .mapToInt(entry -> entry.getKey().credits() * entry.getValue().valueForAverage())
                .sum();
    }
}
