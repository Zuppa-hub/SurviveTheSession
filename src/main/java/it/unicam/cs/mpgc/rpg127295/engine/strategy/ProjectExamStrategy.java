package it.unicam.cs.mpgc.rpg127295.engine.strategy;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;
import it.unicam.cs.mpgc.rpg127295.engine.battle.QuestionPool;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import java.util.Optional;
import java.util.OptionalInt;

/** Project exam: any question, no timer, but every mistake costs double energy. */
public final class ProjectExamStrategy implements ExamStrategy {

    private static final int MISTAKE_MULTIPLIER = 2;

    @Override
    public Optional<Question> nextQuestion(BattleState state, QuestionPool pool) {
        return pool.drawAny();
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
