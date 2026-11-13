package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AnswerFeedbackTest {

    @Test
    void aWrongAnswerNotGivenByThePlayerIsAnExpiredTime() {
        assertAll(
                () -> assertEquals(AnswerFeedback.CORRECT, AnswerFeedback.of(true, true)),
                () -> assertEquals(AnswerFeedback.WRONG, AnswerFeedback.of(false, true)),
                () -> assertEquals(AnswerFeedback.TIME_UP, AnswerFeedback.of(false, false)));
    }

    @Test
    void describesTheAnswerInItalianWithAStyle() {
        assertAll(
                () -> assertEquals("Risposta giusta!", AnswerFeedback.CORRECT.message()),
                () -> assertEquals("Risposta sbagliata.", AnswerFeedback.WRONG.message()),
                () -> assertEquals("Tempo scaduto: conta come risposta sbagliata.", AnswerFeedback.TIME_UP.message()),
                () -> assertEquals("feedback-correct", AnswerFeedback.CORRECT.styleClass()),
                () -> assertEquals("feedback-wrong", AnswerFeedback.TIME_UP.styleClass()));
    }
}
