package it.unicam.cs.mpgc.rpg127295.engine.strategy;

import it.unicam.cs.mpgc.rpg127295.engine.battle.AnswerResult;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.battle.QuestionPool;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Oral exam: no timer, but after a mistake the professor presses on and the next question is
 * harder than the one just failed. If no question of that difficulty is left, the closest
 * one is asked instead.
 */
public final class OralExamStrategy implements ExamStrategy {

    private static final int MISTAKE_MULTIPLIER = 1;

    @Override
    public Optional<Question> nextQuestion(BattleState state, QuestionPool pool) {
        return state.lastAnswer()
                .filter(answer -> !answer.correct())
                .map(AnswerResult::question)
                .map(failed -> pool.drawClosestTo(failed.difficulty().harder()))
                .orElseGet(pool::drawAny);
    }

    @Override
    public OptionalInt secondsPerQuestion() {
        return OptionalInt.empty();
    }

    @Override
    public int mistakeMultiplier() {
        return MISTAKE_MULTIPLIER;
    }
}
