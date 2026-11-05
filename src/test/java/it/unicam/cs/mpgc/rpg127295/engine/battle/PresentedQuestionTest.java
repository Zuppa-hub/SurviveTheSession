package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CHOICE_OPTIONS;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.fillIn;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.model.question.ChoiceQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class PresentedQuestionTest {

    @Test
    void shufflesTheOptionsOfAChoiceQuestionWithTheInjectedGenerator() {
        ChoiceQuestion question = choice("Q-1");

        PresentedQuestion presented = PresentedQuestion.of(question, FakeRandom.first());

        assertAll(
                () -> assertSame(question, presented.question()),
                () -> assertEquals(new HashSet<>(CHOICE_OPTIONS), new HashSet<>(presented.options())),
                () -> assertNotEquals(CHOICE_OPTIONS, presented.options()));
    }

    @Test
    void doesNotChangeTheQuestion() {
        ChoiceQuestion question = choice("Q-1");

        PresentedQuestion.of(question, FakeRandom.first());

        assertEquals(CHOICE_OPTIONS, question.options());
    }

    @Test
    void aFillInQuestionHasNoOptions() {
        PresentedQuestion presented = PresentedQuestion.of(fillIn("Q-1", Difficulty.EASY), FakeRandom.first());

        assertTrue(presented.options().isEmpty());
    }

    @Test
    void optionsCannotBeModified() {
        PresentedQuestion presented = PresentedQuestion.of(choice("Q-1"), FakeRandom.first());

        assertThrows(UnsupportedOperationException.class, () -> presented.options().clear());
    }

    @Test
    void rejectsOptionsThatAreNotThoseOfTheQuestion() {
        ChoiceQuestion question = choice("Q-1");

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new PresentedQuestion(question, List.of("giusta", "altro", "errata", "falsa"))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new PresentedQuestion(fillIn("Q-2", Difficulty.EASY), List.of("giusta"))));
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> PresentedQuestion.of(null, FakeRandom.first())),
                () -> assertThrows(NullPointerException.class, () -> PresentedQuestion.of(choice("Q-1"), null)),
                () -> assertThrows(NullPointerException.class, () -> new PresentedQuestion(choice("Q-1"), null)));
    }
}
