package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CHOICE_OPTIONS;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CORRECT;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.WRONG;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choiceWithHint;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.fillIn;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.trueFalse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.model.question.ChoiceQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    void aNewPresentationHasTheHintHidden() {
        assertFalse(PresentedQuestion.of(choiceWithHint("Q-1"), FakeRandom.first()).hintVisible());
    }

    @Test
    void wrongOptionsAreTheShownOptionsExceptTheCorrectOne() {
        PresentedQuestion presented = PresentedQuestion.of(choice("Q-1"), FakeRandom.first());

        assertEquals(Set.of(WRONG, "errata", "falsa"), new HashSet<>(presented.wrongOptions()));
    }

    @Test
    void removesWrongOptionsChosenWithTheGenerator() {
        PresentedQuestion presented = new PresentedQuestion(choice("Q-1"), CHOICE_OPTIONS, false);

        PresentedQuestion reduced = presented.withoutWrongOptions(2, FakeRandom.first());

        assertAll(
                () -> assertEquals(List.of(CORRECT, "falsa"), reduced.options()),
                () -> assertEquals(CHOICE_OPTIONS, presented.options()));
    }

    @Test
    void cannotRemoveMoreWrongOptionsThanThoseShown() {
        PresentedQuestion trueFalse = PresentedQuestion.of(trueFalse("Q-1"), FakeRandom.first());

        assertThrows(IllegalArgumentException.class, () -> trueFalse.withoutWrongOptions(2, FakeRandom.first()));
    }

    @Test
    void showsTheHintOnce() {
        PresentedQuestion shown = PresentedQuestion.of(choiceWithHint("Q-1"), FakeRandom.first()).withHintShown();

        assertAll(
                () -> assertTrue(shown.hintVisible()),
                () -> assertThrows(IllegalStateException.class, shown::withHintShown));
    }

    @Test
    void acceptsASubsetOfTheOptionsWithTheCorrectOne() {
        PresentedQuestion presented = new PresentedQuestion(choice("Q-1"), List.of("falsa", CORRECT), false);

        assertEquals(List.of("falsa", CORRECT), presented.options());
    }

    @Test
    void rejectsOptionsThatAreNotAValidSubset() {
        ChoiceQuestion question = choice("Q-1");

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new PresentedQuestion(question, List.of(CORRECT, "altro"), false)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new PresentedQuestion(question, List.of(WRONG, "errata"), false)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new PresentedQuestion(question, List.of(CORRECT, CORRECT), false)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new PresentedQuestion(fillIn("Q-2", Difficulty.EASY), List.of(CORRECT), false)));
    }

    @Test
    void rejectsAVisibleHintForAQuestionWithoutHint() {
        assertThrows(IllegalArgumentException.class,
                () -> new PresentedQuestion(choice("Q-1"), CHOICE_OPTIONS, true));
    }

    @Test
    void rejectsNullArguments() {
        PresentedQuestion presented = PresentedQuestion.of(choice("Q-1"), FakeRandom.first());

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> PresentedQuestion.of(null, FakeRandom.first())),
                () -> assertThrows(NullPointerException.class, () -> PresentedQuestion.of(choice("Q-1"), null)),
                () -> assertThrows(NullPointerException.class, () -> new PresentedQuestion(choice("Q-1"), null, false)),
                () -> assertThrows(NullPointerException.class, () -> presented.withoutWrongOptions(1, null)));
    }
}
