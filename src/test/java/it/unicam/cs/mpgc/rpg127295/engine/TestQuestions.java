package it.unicam.cs.mpgc.rpg127295.engine;

import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.FillInQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.MultipleChoiceQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import it.unicam.cs.mpgc.rpg127295.model.question.TrueFalseQuestion;
import java.util.List;
import java.util.Set;

/** Questions for the engine tests: the correct answer is always {@link #CORRECT}. */
public final class TestQuestions {

    public static final Subject SUBJECT = new Subject("PROG", "Programmazione");
    public static final String CORRECT = "giusta";
    public static final String WRONG = "sbagliata";
    public static final String HINT = "Pensa allo stack";
    public static final List<String> CHOICE_OPTIONS = List.of(CORRECT, WRONG, "errata", "falsa");

    private TestQuestions() {
    }

    public static MultipleChoiceQuestion choice(String id, Difficulty difficulty) {
        return new MultipleChoiceQuestion(details(id, difficulty), CHOICE_OPTIONS, CORRECT);
    }

    public static MultipleChoiceQuestion choice(String id) {
        return choice(id, Difficulty.MEDIUM);
    }

    /** @return a choice question with the hint {@link #HINT} */
    public static MultipleChoiceQuestion choiceWithHint(String id) {
        QuestionDetails details = QuestionDetails.withHint(id, SUBJECT, Difficulty.MEDIUM, "Domanda " + id, HINT);
        return new MultipleChoiceQuestion(details, CHOICE_OPTIONS, CORRECT);
    }

    /** @return a true/false question whose correct answer is "Vero" */
    public static TrueFalseQuestion trueFalse(String id) {
        return new TrueFalseQuestion(details(id, Difficulty.MEDIUM), true);
    }

    public static FillInQuestion fillIn(String id, Difficulty difficulty) {
        return new FillInQuestion(details(id, difficulty), Set.of(CORRECT));
    }

    private static QuestionDetails details(String id, Difficulty difficulty) {
        return QuestionDetails.withoutHint(id, SUBJECT, difficulty, "Domanda " + id);
    }
}
