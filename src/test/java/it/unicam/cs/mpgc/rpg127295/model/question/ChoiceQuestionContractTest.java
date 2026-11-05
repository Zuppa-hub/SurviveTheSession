package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Checks the contract of {@link ChoiceQuestion} that every implementation must respect (LSP),
 * in addition to the general {@link Question} contract.
 */
abstract class ChoiceQuestionContractTest extends QuestionContractTest {

    @Override
    abstract ChoiceQuestion createQuestion(String id, String prompt);

    private ChoiceQuestion question() {
        return createQuestion("Q-1", "Domanda");
    }

    @Test
    void hasAtLeastTwoOptions() {
        assertTrue(question().options().size() >= 2);
    }

    @Test
    void correctAnswerIsOneOfTheOptions() {
        assertTrue(question().options().contains(correctAnswer()));
    }

    @Test
    void wrongOptionsAreAllOptionsExceptTheCorrectOne() {
        ChoiceQuestion question = question();
        List<String> wrongOptions = question.wrongOptions();

        assertAll(
                () -> assertTrue(question.options().containsAll(wrongOptions)),
                () -> assertFalse(wrongOptions.contains(correctAnswer())),
                () -> assertEquals(question.options().size() - 1, wrongOptions.size()));
    }

    @Test
    void everyWrongOptionIsRejected() {
        ChoiceQuestion question = question();

        question.wrongOptions().forEach(option -> assertFalse(question.isCorrect(option)));
    }

    @Test
    void comparesAnswersExactly() {
        ChoiceQuestion question = question();

        assertAll(
                () -> assertFalse(question.isCorrect(" " + correctAnswer())),
                () -> assertFalse(question.isCorrect(correctAnswer() + " ")));
    }

    @Test
    void isVisitedAsAChoiceQuestion() {
        assertEquals(CategoryVisitor.CHOICE, question().accept(new CategoryVisitor()));
    }

    @Test
    void optionsCannotBeModified() {
        ChoiceQuestion question = question();

        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> question.options().add("x")),
                () -> assertThrows(UnsupportedOperationException.class, () -> question.wrongOptions().clear()));
    }
}
