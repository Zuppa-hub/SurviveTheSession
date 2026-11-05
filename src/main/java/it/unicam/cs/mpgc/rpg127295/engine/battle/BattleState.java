package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable snapshot of a battle, given to the strategies and to the listeners.
 *
 * @param resistance      the resistance left, from 0 to {@code maxResistance}
 * @param maxResistance   the resistance at the start of the battle; positive
 * @param stats           the stats of the student
 * @param performance     the answers given so far
 * @param currentQuestion the question waiting for an answer, if any
 * @param lastAnswer      the result of the last answer, if any
 * @param timed           whether the current question has a timer; false without a question
 * @param nextCorrectAnswerDoubled whether the next correct answer does boosted damage
 */
public record BattleState(int resistance, int maxResistance, Stats stats, Performance performance,
                          Optional<PresentedQuestion> currentQuestion, Optional<AnswerResult> lastAnswer,
                          boolean timed, boolean nextCorrectAnswerDoubled) {

    public BattleState {
        requirePositive(maxResistance, "Max resistance");
        if (resistance < 0 || resistance > maxResistance) {
            throw new IllegalArgumentException(
                    "Resistance must be between 0 and " + maxResistance + ", was " + resistance);
        }
        Objects.requireNonNull(stats, "Stats must not be null");
        Objects.requireNonNull(performance, "Performance must not be null");
        Objects.requireNonNull(currentQuestion, "Current question must not be null");
        Objects.requireNonNull(lastAnswer, "Last answer must not be null");
        if (timed && currentQuestion.isEmpty()) {
            throw new IllegalArgumentException("Only a question waiting for an answer can be timed");
        }
    }

    public boolean isExamDefeated() {
        return resistance == 0;
    }
}
