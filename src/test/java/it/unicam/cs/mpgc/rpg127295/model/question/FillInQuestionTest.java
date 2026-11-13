package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FillInQuestionTest extends QuestionContractTest {

    private static final Set<String> ACCEPTED = Set.of("LIFO", "Last In First Out");

    @Override
    FillInQuestion createQuestion(String id, String prompt) {
        return new FillInQuestion(details(id, prompt), ACCEPTED);
    }

    @Override
    String correctAnswer() {
        return "LIFO";
    }

    @Override
    String wrongAnswer() {
        return "FIFO";
    }

    private FillInQuestion question() {
        return createQuestion("Q-1", "Con quale politica funziona uno stack?");
    }

    @ParameterizedTest
    @ValueSource(strings = {"lifo", "  LIFO  ", "Lifo", "\tlifo\n"})
    void ignoresCaseAndSurroundingSpaces(String answer) {
        assertTrue(question().isCorrect(answer));
    }

    @ParameterizedTest
    @ValueSource(strings = {"last in first out", "LAST  IN   FIRST OUT", " last\tin first\nout "})
    void collapsesInnerWhitespace(String answer) {
        assertTrue(question().isCorrect(answer));
    }

    @Test
    void acceptsEveryAcceptedAnswer() {
        assertAll(
                () -> assertTrue(question().isCorrect("LIFO")),
                () -> assertTrue(question().isCorrect("Last In First Out")));
    }

    @Test
    void doesNotIgnorePunctuationOrMissingSpaces() {
        assertAll(
                () -> assertFalse(question().isCorrect("L.I.F.O.")),
                () -> assertFalse(question().isCorrect("lastinfirstout")));
    }

    @Test
    void copiesTheGivenAnswers() {
        Set<String> answers = new HashSet<>(Set.of("LIFO"));
        FillInQuestion question = new FillInQuestion(details("Q-1", "Domanda"), answers);

        answers.add("FIFO");

        assertFalse(question.isCorrect("FIFO"));
    }

    @Test
    void requiresAtLeastOneAnswer() {
        assertThrows(IllegalArgumentException.class,
                () -> new FillInQuestion(details("Q-1", "Domanda"), Set.of()));
    }

    @Test
    void rejectsBlankAnswers() {
        assertThrows(IllegalArgumentException.class,
                () -> new FillInQuestion(details("Q-1", "Domanda"), Set.of("LIFO", "  ")));
    }

    @Test
    void rejectsMissingReferences() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new FillInQuestion(details("Q-1", "Domanda"), null)),
                () -> assertThrows(NullPointerException.class,
                        () -> new FillInQuestion(details("Q-1", "Domanda"), new HashSet<>(Arrays.asList("LIFO", null)))),
                () -> assertThrows(NullPointerException.class, () -> new FillInQuestion(null, ACCEPTED)));
    }

    @Test
    void isVisitedAsAFillInQuestion() {
        assertEquals(CategoryVisitor.FILL_IN, question().accept(new CategoryVisitor()));
    }
}
