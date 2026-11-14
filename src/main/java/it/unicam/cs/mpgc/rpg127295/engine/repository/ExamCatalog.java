package it.unicam.cs.mpgc.rpg127295.engine.repository;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import java.util.List;
import java.util.Optional;

/** The exams of the session. Defined here, where it is used, and implemented in persistence. */
public interface ExamCatalog {

    /** @return every exam, in session order; unmodifiable */
    List<Exam> all();

    /** @return the exam with the given code, or empty if none exists */
    Optional<Exam> findByCode(String code);
}
