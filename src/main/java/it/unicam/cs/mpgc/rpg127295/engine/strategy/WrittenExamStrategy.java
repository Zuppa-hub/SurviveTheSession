package it.unicam.cs.mpgc.rpg127295.engine.strategy;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;
import it.unicam.cs.mpgc.rpg127295.engine.battle.QuestionPool;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import java.util.Optional;
import java.util.OptionalInt;

/** Written exam: any question, with a time limit for each one. */
public final class WrittenExamStrategy implements ExamStrategy {

    private static final int SECONDS_PER_QUESTION = 30;
    private static final int MISTAKE_MULTIPLIER = 1;

    @Override
    public Optional<Question> nextQuestion(BattleState state, QuestionPool pool) {
        return pool.drawAny();
    }

    @Override
    public OptionalInt secondsPerQuestion() {
        return OptionalInt.of(SECONDS_PER_QUESTION);
    }

    @Override
    public int mistakeMultiplier() {
        return MISTAKE_MULTIPLIER;
    }
}
