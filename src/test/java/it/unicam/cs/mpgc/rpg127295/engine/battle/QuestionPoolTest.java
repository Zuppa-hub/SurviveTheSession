package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.fillIn;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.EASY;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.HARD;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.MEDIUM;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class QuestionPoolTest {

    private static final Question EASY_QUESTION = choice("Q-E", EASY);
    private static final Question MEDIUM_QUESTION = fillIn("Q-M", MEDIUM);
    private static final Question HARD_QUESTION = choice("Q-H", HARD);

    private static QuestionPool poolOf(Question... questions) {
        return new QuestionPool(List.of(questions), FakeRandom.first());
    }

    private static Question byDifficulty(Difficulty difficulty) {
        return Stream.of(EASY_QUESTION, MEDIUM_QUESTION, HARD_QUESTION)
                .filter(question -> question.difficulty() == difficulty)
                .findFirst()
                .orElseThrow();
    }

    @Test
    void drawAnyNeverRepeatsAQuestion() {
        QuestionPool pool = poolOf(EASY_QUESTION, MEDIUM_QUESTION, HARD_QUESTION);

        Set<Question> drawn = new HashSet<>();
        Stream.generate(pool::drawAny).limit(3).forEach(question -> drawn.add(question.orElseThrow()));

        assertAll(
                () -> assertEquals(Set.of(EASY_QUESTION, MEDIUM_QUESTION, HARD_QUESTION), drawn),
                () -> assertEquals(Optional.empty(), pool.drawAny()),
                () -> assertTrue(pool.isEmpty()));
    }

    @Test
    void drawAnyChoosesWithTheInjectedGenerator() {
        List<Question> questions = List.of(EASY_QUESTION, MEDIUM_QUESTION, HARD_QUESTION);

        assertAll(
                () -> assertEquals(Optional.of(EASY_QUESTION), new QuestionPool(questions, FakeRandom.first()).drawAny()),
                () -> assertEquals(Optional.of(HARD_QUESTION), new QuestionPool(questions, FakeRandom.last()).drawAny()));
    }

    @Test
    void aQuestionWithTheSameIdCountsAsAlreadyAsked() {
        QuestionPool pool = poolOf(EASY_QUESTION, choice("Q-E", HARD));

        pool.drawAny();

        assertEquals(Optional.empty(), pool.drawAny());
    }

    @Test
    void drawClosestToPrefersTheRequestedDifficulty() {
        QuestionPool pool = poolOf(EASY_QUESTION, MEDIUM_QUESTION, HARD_QUESTION);

        assertEquals(Optional.of(MEDIUM_QUESTION), pool.drawClosestTo(MEDIUM));
    }

    @ParameterizedTest(name = "requested {0}, available {1} and {2} -> {3}")
    @CsvSource({
        "MEDIUM, EASY,   HARD,   HARD",
        "HARD,   EASY,   MEDIUM, MEDIUM",
        "EASY,   MEDIUM, HARD,   MEDIUM",
        "EASY,   HARD,   HARD,   HARD"
    })
    void drawClosestToFallsBackToTheNearestDifficultyPreferringTheHarder(
            Difficulty requested, Difficulty first, Difficulty second, Difficulty expected) {
        QuestionPool pool = poolOf(byDifficulty(first), byDifficulty(second));

        assertEquals(expected, pool.drawClosestTo(requested).orElseThrow().difficulty());
    }

    @Test
    void drawClosestToNeverRepeatsAQuestion() {
        QuestionPool pool = poolOf(MEDIUM_QUESTION, HARD_QUESTION);

        pool.drawClosestTo(MEDIUM);

        assertAll(
                () -> assertEquals(Optional.of(HARD_QUESTION), pool.drawClosestTo(MEDIUM)),
                () -> assertEquals(Optional.empty(), pool.drawClosestTo(MEDIUM)));
    }

    @Test
    void countsTheRemainingQuestions() {
        QuestionPool pool = poolOf(EASY_QUESTION, MEDIUM_QUESTION);

        pool.drawAny();

        assertAll(
                () -> assertEquals(1, pool.remaining()),
                () -> assertFalse(pool.isEmpty()));
    }

    @Test
    void anEmptyPoolHasNothingToDraw() {
        QuestionPool pool = poolOf();

        assertAll(
                () -> assertTrue(pool.isEmpty()),
                () -> assertEquals(Optional.empty(), pool.drawAny()),
                () -> assertEquals(Optional.empty(), pool.drawClosestTo(EASY)));
    }

    @Test
    void isNotAffectedByChangesToTheOriginalList() {
        List<Question> questions = new ArrayList<>(List.of(EASY_QUESTION));
        QuestionPool pool = new QuestionPool(questions, FakeRandom.first());

        questions.add(HARD_QUESTION);

        assertEquals(1, pool.remaining());
    }

    @Test
    void rejectsNullArguments() {
        QuestionPool pool = poolOf(EASY_QUESTION);

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new QuestionPool(null, FakeRandom.first())),
                () -> assertThrows(NullPointerException.class, () -> new QuestionPool(List.of(), null)),
                () -> assertThrows(NullPointerException.class, () -> pool.drawClosestTo(null)));
    }
}
