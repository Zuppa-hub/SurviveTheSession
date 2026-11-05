package it.unicam.cs.mpgc.rpg127295.engine.strategy;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;
import it.unicam.cs.mpgc.rpg127295.engine.battle.QuestionPool;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * The behaviour of a type of exam (Strategy). A new type of exam is a new implementation plus
 * an entry in the {@code EnumMap<ExamType, ExamStrategy>} built by the application: the
 * engine never checks the type of an exam (OCP).
 * <p>
 * Contract of the type, valid for every implementation:
 * <ul>
 *   <li>{@link #nextQuestion} draws from the pool, so it never returns a question already
 *       asked, and returns empty only if the pool is empty;</li>
 *   <li>{@link #secondsPerQuestion()}, when present, is positive;</li>
 *   <li>{@link #mistakeMultiplier()} is at least 1.</li>
 * </ul>
 */
public interface ExamStrategy {

    /** @return the next question, or empty if no question is left */
    Optional<Question> nextQuestion(BattleState state, QuestionPool pool);

    /** @return the time to answer each question, or empty for no timer */
    OptionalInt secondsPerQuestion();

    /** @return the factor applied to the energy lost for a mistake */
    int mistakeMultiplier();
}
