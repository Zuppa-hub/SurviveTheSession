package it.unicam.cs.mpgc.rpg127295.ui;

/** What the exam screen says after an answer, and the style it uses. */
public enum AnswerFeedback {
    CORRECT("Risposta giusta!", "feedback-correct"),
    WRONG("Risposta sbagliata.", "feedback-wrong"),
    TIME_UP("Tempo scaduto: conta come risposta sbagliata.", "feedback-wrong");

    private final String message;
    private final String styleClass;

    AnswerFeedback(String message, String styleClass) {
        this.message = message;
        this.styleClass = styleClass;
    }

    /**
     * The battle reports an expired time as a wrong answer; only the screen knows whether the
     * player had answered.
     *
     * @param correct          whether the answer was correct
     * @param answeredByPlayer whether the player gave the answer, false if the time ran out
     */
    public static AnswerFeedback of(boolean correct, boolean answeredByPlayer) {
        if (correct) {
            return CORRECT;
        }
        return answeredByPlayer ? WRONG : TIME_UP;
    }

    public String message() {
        return message;
    }

    public String styleClass() {
        return styleClass;
    }
}
