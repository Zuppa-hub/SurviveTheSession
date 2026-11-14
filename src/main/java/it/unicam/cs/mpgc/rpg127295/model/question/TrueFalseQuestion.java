package it.unicam.cs.mpgc.rpg127295.model.question;

import java.util.List;

/**
 * A statement the player marks as true or false.
 * <p>
 * Its options are always "Vero" and "Falso". With a single wrong option it does not meet the
 * 50/50 rule of {@link ChoiceQuestion}, so Notes cannot be used on it.
 */
public final class TrueFalseQuestion extends AbstractChoiceQuestion {

    private static final String TRUE_OPTION = "Vero";
    private static final String FALSE_OPTION = "Falso";
    private static final List<String> OPTIONS = List.of(TRUE_OPTION, FALSE_OPTION);

    /**
     * @param statementIsTrue whether the statement in the prompt is true
     * @throws NullPointerException if details is null
     */
    public TrueFalseQuestion(QuestionDetails details, boolean statementIsTrue) {
        super(details, OPTIONS, statementIsTrue ? TRUE_OPTION : FALSE_OPTION);
    }
}
