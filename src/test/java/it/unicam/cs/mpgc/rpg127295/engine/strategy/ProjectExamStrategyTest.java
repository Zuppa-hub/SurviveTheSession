package it.unicam.cs.mpgc.rpg127295.engine.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.battle.QuestionPool;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class ProjectExamStrategyTest extends ExamStrategyContractTest {

    @Override
    ExamStrategy createStrategy() {
        return new ProjectExamStrategy();
    }

    @Test
    void hasNoTimer() {
        assertEquals(OptionalInt.empty(), createStrategy().secondsPerQuestion());
    }

    @Test
    void mistakesCostDoubleEnergy() {
        assertEquals(2, createStrategy().mistakeMultiplier());
    }

    @Test
    void asksQuestionsOfEveryKind() {
        QuestionPool pool = new QuestionPool(QUESTIONS, FakeRandom.first());
        pool.drawAny();

        assertEquals(Optional.of(QUESTIONS.get(1)), createStrategy().nextQuestion(initialState(), pool));
    }
}
