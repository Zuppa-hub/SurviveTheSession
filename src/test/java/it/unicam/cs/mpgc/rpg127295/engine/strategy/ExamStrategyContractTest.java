package it.unicam.cs.mpgc.rpg127295.engine.strategy;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.fillIn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.AnswerResult;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.battle.QuestionPool;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Checks the contract of {@link ExamStrategy} that every strategy must respect (LSP).
 * Each strategy test extends this class and provides its strategy.
 */
abstract class ExamStrategyContractTest {

    static final List<Question> QUESTIONS = List.of(
            choice("Q-E", Difficulty.EASY),
            fillIn("Q-M", Difficulty.MEDIUM),
            choice("Q-H", Difficulty.HARD));

    abstract ExamStrategy createStrategy();

    /** @return a state with no answer given yet */
    static BattleState initialState() {
        return state(Optional.empty());
    }

    /** @return a state whose last answer was the given one */
    static BattleState stateAfter(Question question, boolean correct) {
        return state(Optional.of(new AnswerResult(question, correct)));
    }

    private static BattleState state(Optional<AnswerResult> lastAnswer) {
        return new BattleState(10, 10, Stats.initial(5, 10, 4), new Performance(0, 0, 0),
                Optional.empty(), lastAnswer, false, false);
    }

    static Stream<BattleState> states() {
        return Stream.of(
                initialState(),
                stateAfter(QUESTIONS.get(1), true),
                stateAfter(QUESTIONS.get(1), false),
                stateAfter(QUESTIONS.get(2), false));
    }

    @ParameterizedTest
    @MethodSource("states")
    void drawsEveryQuestionOnceAndThenNothing(BattleState state) {
        ExamStrategy strategy = createStrategy();
        QuestionPool pool = new QuestionPool(QUESTIONS, FakeRandom.first());

        Set<Question> drawn = new HashSet<>();
        QUESTIONS.forEach(ignored -> drawn.add(strategy.nextQuestion(state, pool).orElseThrow()));

        assertEquals(Set.copyOf(QUESTIONS), drawn);
        assertEquals(Optional.empty(), strategy.nextQuestion(state, pool));
    }

    @Test
    void theSecondsPerQuestionArePositiveWhenPresent() {
        createStrategy().secondsPerQuestion().ifPresent(seconds -> assertTrue(seconds > 0));
    }

    @Test
    void theMistakeMultiplierIsAtLeastOne() {
        assertTrue(createStrategy().mistakeMultiplier() >= 1);
    }
}
