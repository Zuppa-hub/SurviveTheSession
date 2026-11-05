package it.unicam.cs.mpgc.rpg127295.engine.strategy;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.EASY;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.HARD;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.MEDIUM;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.QuestionPool;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OralExamStrategyTest extends ExamStrategyContractTest {

    private static final Question ASKED_MEDIUM = choice("A-M", MEDIUM);
    private static final Question ASKED_HARD = choice("A-H", HARD);

    @Override
    ExamStrategy createStrategy() {
        return new OralExamStrategy();
    }

    private Difficulty nextDifficultyAfter(Question question, boolean correct, List<Question> available) {
        QuestionPool pool = new QuestionPool(available, FakeRandom.first());
        return createStrategy().nextQuestion(stateAfter(question, correct), pool).orElseThrow().difficulty();
    }

    @Test
    void hasNoTimer() {
        assertEquals(OptionalInt.empty(), createStrategy().secondsPerQuestion());
    }

    @Test
    void mistakesCostTheBaseEnergy() {
        assertEquals(1, createStrategy().mistakeMultiplier());
    }

    @ParameterizedTest(name = "mistake on {0} -> {1}")
    @CsvSource({
        "EASY,   MEDIUM",
        "MEDIUM, HARD",
        "HARD,   HARD"
    })
    void afterAMistakeTheNextQuestionIsHarder(Difficulty failed, Difficulty expected) {
        Question question = choice("A-1", failed);

        assertEquals(expected, nextDifficultyAfter(question, false, QUESTIONS));
    }

    @Test
    void afterACorrectAnswerAnyQuestionCanFollow() {
        assertEquals(EASY, nextDifficultyAfter(ASKED_MEDIUM, true, QUESTIONS));
    }

    @Test
    void withoutHarderQuestionsLeftTheClosestOneFollows() {
        List<Question> easyAndMedium = List.of(QUESTIONS.get(0), QUESTIONS.get(1));

        assertEquals(MEDIUM, nextDifficultyAfter(ASKED_MEDIUM, false, easyAndMedium));
    }

    @Test
    void theFirstQuestionCanBeAny() {
        QuestionPool pool = new QuestionPool(QUESTIONS, FakeRandom.first());

        assertEquals(EASY, createStrategy().nextQuestion(initialState(), pool).orElseThrow().difficulty());
    }

    @Test
    void afterAMistakeOnAHardQuestionOnlyEasyOnesMayBeLeft() {
        assertEquals(EASY, nextDifficultyAfter(ASKED_HARD, false, List.of(QUESTIONS.get(0))));
    }
}
