package it.unicam.cs.mpgc.rpg127295.engine.repository;

import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.List;

/** The questions of each subject. Defined here, where it is used, and implemented in persistence. */
public interface QuestionRepository {

    /** @return the questions of the subject in content order, empty if it has none; unmodifiable */
    List<Question> findBySubject(Subject subject);
}
