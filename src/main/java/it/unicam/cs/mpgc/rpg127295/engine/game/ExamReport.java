package it.unicam.cs.mpgc.rpg127295.engine.game;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonNegative;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleOutcome;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import java.util.Objects;

/**
 * What happened in an exam, after the game has applied it: shown by the outcome screen.
 *
 * @param exam         the exam
 * @param outcome      how the battle ended
 * @param reward       the reward; {@link Reward#none()} if the exam was not passed
 * @param levelsGained the levels gained with the experience of the reward
 */
public record ExamReport(Exam exam, BattleOutcome outcome, Reward reward, int levelsGained) {

    public ExamReport {
        Objects.requireNonNull(exam, "Exam must not be null");
        Objects.requireNonNull(outcome, "Outcome must not be null");
        Objects.requireNonNull(reward, "Reward must not be null");
        requireNonNegative(levelsGained, "Levels gained");
    }
}
