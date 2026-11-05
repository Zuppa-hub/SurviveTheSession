package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.engine.repository.QuestionRepository;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Creates the battle of an exam with the strategy of its type. The association between types
 * and strategies is an {@link EnumMap} built by the application: this is the only place that
 * looks it up, and no code checks the type of an exam with a {@code switch} (OCP).
 */
public final class ExamBattleFactory {

    private final Map<ExamType, ExamStrategy> strategies;
    private final QuestionRepository questions;
    private final BattleServices services;

    /**
     * @param strategies a strategy for every {@link ExamType}; the map is copied
     * @param questions  where the questions of each subject come from
     * @param services   the collaborators shared by every battle
     * @throws NullPointerException     if an argument is null
     * @throws IllegalArgumentException if a type of exam has no strategy
     */
    public ExamBattleFactory(Map<ExamType, ExamStrategy> strategies, QuestionRepository questions,
                             BattleServices services) {
        this.strategies = completeCopyOf(strategies);
        this.questions = Objects.requireNonNull(questions, "Question repository must not be null");
        this.services = Objects.requireNonNull(services, "Battle services must not be null");
    }

    /** @return a new battle, not begun yet, with the questions of the subject of the exam */
    public ExamBattle create(Student student, Exam exam) {
        Objects.requireNonNull(student, "Student must not be null");
        Objects.requireNonNull(exam, "Exam must not be null");
        QuestionPool pool = new QuestionPool(questions.findBySubject(exam.subject()), services.random());
        return new ExamBattle(student, exam, strategies.get(exam.type()), pool, services);
    }

    private static Map<ExamType, ExamStrategy> completeCopyOf(Map<ExamType, ExamStrategy> strategies) {
        Objects.requireNonNull(strategies, "Strategies must not be null");
        Set<ExamType> missing = EnumSet.allOf(ExamType.class);
        missing.removeAll(strategies.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("No strategy for exam types " + missing);
        }
        return new EnumMap<>(strategies);
    }
}
