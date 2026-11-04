package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class QuestionDetailsTest {

    private static final Subject PROGRAMMING = new Subject("PROG", "Programmazione");

    @Test
    void withHintExposesAllFields() {
        QuestionDetails details = QuestionDetails.withHint(
                "PROG-01", PROGRAMMING, Difficulty.EASY, "Cos'è una classe?", "Pensa a uno stampo");

        assertAll(
                () -> assertEquals("PROG-01", details.id()),
                () -> assertEquals(PROGRAMMING, details.subject()),
                () -> assertEquals(Difficulty.EASY, details.difficulty()),
                () -> assertEquals("Cos'è una classe?", details.prompt()),
                () -> assertEquals(Optional.of("Pensa a uno stampo"), details.hint()));
    }

    @Test
    void withoutHintHasEmptyHint() {
        QuestionDetails details = QuestionDetails.withoutHint(
                "PROG-01", PROGRAMMING, Difficulty.EASY, "Cos'è una classe?");

        assertEquals(Optional.empty(), details.hint());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankId(String id) {
        assertThrows(IllegalArgumentException.class,
                () -> QuestionDetails.withoutHint(id, PROGRAMMING, Difficulty.EASY, "Cos'è una classe?"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankPrompt(String prompt) {
        assertThrows(IllegalArgumentException.class,
                () -> QuestionDetails.withoutHint("PROG-01", PROGRAMMING, Difficulty.EASY, prompt));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsBlankHintWhenPresent(String hint) {
        assertThrows(IllegalArgumentException.class, () -> QuestionDetails.withHint(
                "PROG-01", PROGRAMMING, Difficulty.EASY, "Cos'è una classe?", hint));
    }

    @Test
    void rejectsMissingReferences() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> QuestionDetails.withoutHint("PROG-01", null, Difficulty.EASY, "Domanda")),
                () -> assertThrows(NullPointerException.class,
                        () -> QuestionDetails.withoutHint("PROG-01", PROGRAMMING, null, "Domanda")),
                () -> assertThrows(NullPointerException.class,
                        () -> new QuestionDetails("PROG-01", PROGRAMMING, Difficulty.EASY, "Domanda", null)));
    }
}
