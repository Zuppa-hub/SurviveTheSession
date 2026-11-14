package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Checks the contract of {@link Question} that every implementation must respect (LSP).
 * Each concrete question test extends this class and provides its own instances.
 */
abstract class QuestionContractTest {

    static final Subject PROGRAMMING = new Subject("PROG", "Programmazione");
    static final String HINT = "Rileggi le slide";

    /** @return a question with the given id, difficulty MEDIUM, subject PROGRAMMING and {@link #HINT} */
    abstract Question createQuestion(String id, String prompt);

    /** @return an answer that {@link #createQuestion} accepts */
    abstract String correctAnswer();

    /** @return an answer that {@link #createQuestion} rejects */
    abstract String wrongAnswer();

    static QuestionDetails details(String id, String prompt) {
        return QuestionDetails.withHint(id, PROGRAMMING, Difficulty.MEDIUM, prompt, HINT);
    }

    private Question question() {
        return createQuestion("Q-1", "Domanda");
    }

    @Test
    void exposesItsDetails() {
        Question question = question();

        assertAll(
                () -> assertEquals("Q-1", question.id()),
                () -> assertEquals(PROGRAMMING, question.subject()),
                () -> assertEquals(Difficulty.MEDIUM, question.difficulty()),
                () -> assertEquals("Domanda", question.prompt()),
                () -> assertEquals(Optional.of(HINT), question.hint()));
    }

    @Test
    void acceptsTheCorrectAnswer() {
        assertTrue(question().isCorrect(correctAnswer()));
    }

    @Test
    void rejectsAWrongAnswer() {
        assertFalse(question().isCorrect(wrongAnswer()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "risposta che non esiste"})
    void rejectsMissingOrUnknownAnswersWithoutThrowing(String answer) {
        assertFalse(question().isCorrect(answer));
    }

    @Test
    void questionsWithSameIdAreEqualEvenWithDifferentData() {
        Question first = createQuestion("Q-1", "Domanda");
        Question second = createQuestion("Q-1", "Altra domanda");

        assertAll(
                () -> assertEquals(first, second),
                () -> assertEquals(second, first),
                () -> assertEquals(first.hashCode(), second.hashCode()));
    }

    @Test
    void questionsWithSameIdAreEqualEvenWithDifferentType() {
        Question question = question();
        Question otherKind = new AnotherKindOfQuestion(details("Q-1", "Domanda di un altro tipo"));

        assertAll(
                () -> assertEquals(question, otherKind),
                () -> assertEquals(otherKind, question),
                () -> assertEquals(question.hashCode(), otherKind.hashCode()));
    }

    @Test
    void questionsWithDifferentIdAreNotEqual() {
        assertNotEquals(createQuestion("Q-1", "Domanda"), createQuestion("Q-2", "Domanda"));
    }

    @Test
    void isNeverEqualToNullOrOtherObjects() {
        assertAll(
                () -> assertNotEquals(null, question()),
                () -> assertNotEquals("Q-1", question()));
    }

    @Test
    void acceptPassesTheQuestionItselfToTheVisitor() {
        Question question = question();

        assertSame(question, question.accept(new IdentityVisitor()));
    }

    /** A minimal question of a different kind, used to check equality across types. */
    private static final class AnotherKindOfQuestion extends AbstractChoiceQuestion {

        AnotherKindOfQuestion(QuestionDetails details) {
            super(details, List.of("a", "b"), "a");
        }
    }

    /** Returns the visited question, whatever its category. */
    private static final class IdentityVisitor implements QuestionVisitor<Question> {

        @Override
        public Question visitChoice(ChoiceQuestion question) {
            return question;
        }

        @Override
        public Question visitFillIn(FillInQuestion question) {
            return question;
        }
    }
}
