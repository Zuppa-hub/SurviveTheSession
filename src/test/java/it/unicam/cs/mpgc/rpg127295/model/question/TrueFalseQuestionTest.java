package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TrueFalseQuestionTest extends ChoiceQuestionContractTest {

    @Override
    TrueFalseQuestion createQuestion(String id, String prompt) {
        return new TrueFalseQuestion(details(id, prompt), true);
    }

    @Override
    String correctAnswer() {
        return "Vero";
    }

    @Override
    String wrongAnswer() {
        return "Falso";
    }

    @Test
    void optionsAreTrueThenFalse() {
        assertEquals(List.of("Vero", "Falso"), createQuestion("Q-1", "Domanda").options());
    }

    @Test
    void falseStatementAcceptsFalse() {
        TrueFalseQuestion question = new TrueFalseQuestion(details("Q-1", "Domanda"), false);

        assertAll(
                () -> assertTrue(question.isCorrect("Falso")),
                () -> assertFalse(question.isCorrect("Vero")),
                () -> assertEquals(List.of("Vero"), question.wrongOptions()));
    }

    @Test
    void hasTooFewWrongOptionsForFiftyFifty() {
        assertTrue(createQuestion("Q-1", "Domanda").wrongOptions().size() < 3);
    }

    @Test
    void rejectsMissingDetails() {
        assertThrows(NullPointerException.class, () -> new TrueFalseQuestion(null, true));
    }
}
