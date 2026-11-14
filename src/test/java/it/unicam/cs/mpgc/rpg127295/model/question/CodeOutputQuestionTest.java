package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CodeOutputQuestionTest extends ChoiceQuestionContractTest {

    private static final String CODE = """
            int x = 1;
            x += x++;
            System.out.println(x);
            """;
    private static final List<String> OUTPUTS = List.of("1", "2", "3", "4");

    @Override
    CodeOutputQuestion createQuestion(String id, String prompt) {
        return new CodeOutputQuestion(details(id, prompt), CODE, OUTPUTS, "2");
    }

    @Override
    String correctAnswer() {
        return "2";
    }

    @Override
    String wrongAnswer() {
        return "3";
    }

    @Test
    void exposesTheCodeSeparatelyFromThePrompt() {
        CodeOutputQuestion question = createQuestion("Q-1", "Cosa stampa questo codice?");

        assertAll(
                () -> assertEquals(CODE, question.code()),
                () -> assertEquals("Cosa stampa questo codice?", question.prompt()));
    }

    @Test
    void offersTheCodeAsTheSnippetToShowWithThePrompt() {
        assertEquals(Optional.of(CODE), createQuestion("Q-1", "Cosa stampa?").codeSnippet());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\n\t"})
    void rejectsBlankCode(String code) {
        assertThrows(IllegalArgumentException.class,
                () -> new CodeOutputQuestion(details("Q-1", "Domanda"), code, OUTPUTS, "2"));
    }

    @Test
    void requiresExactlyFourOutputs() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new CodeOutputQuestion(details("Q-1", "Domanda"), CODE, List.of("1", "2"), "2")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new CodeOutputQuestion(details("Q-1", "Domanda"), CODE,
                                List.of("1", "2", "3", "4", "5"), "2")));
    }

    @Test
    void rejectsCorrectOutputNotAmongOutputs() {
        assertThrows(IllegalArgumentException.class,
                () -> new CodeOutputQuestion(details("Q-1", "Domanda"), CODE, OUTPUTS, "5"));
    }
}
