package it.unicam.cs.mpgc.rpg127295.engine.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.battle.QuestionPool;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class WrittenExamStrategyTest extends ExamStrategyContractTest {

    @Override
    ExamStrategy createStrategy() {
        return new WrittenExamStrategy();
    }

    @Test
    void givesThirtySecondsPerQuestion() {
        assertEquals(OptionalInt.of(30), createStrategy().secondsPerQuestion());
    }

    @Test
    void mistakesCostTheBaseEnergy() {
        assertEquals(1, createStrategy().mistakeMultiplier());
    }

    @Test
    void choosesAnyQuestionWithTheGenerator() {
        QuestionPool pool = new QuestionPool(QUESTIONS, FakeRandom.last());

        assertEquals(Optional.of(QUESTIONS.get(2)), createStrategy().nextQuestion(initialState(), pool));
    }
}
