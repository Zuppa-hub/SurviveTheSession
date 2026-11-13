package it.unicam.cs.mpgc.rpg127295.model.question;

import java.util.List;

/** A question with exactly four options, one of which is correct. */
public final class MultipleChoiceQuestion extends AbstractChoiceQuestion {

    private static final int OPTION_COUNT = 4;

    /**
     * @param options       exactly four distinct, non-blank options, in display order
     * @param correctOption the correct option; must be one of {@code options}
     * @throws NullPointerException     if a reference or an option is null
     * @throws IllegalArgumentException if the options are not four valid options containing
     *                                  the correct one
     */
    public MultipleChoiceQuestion(QuestionDetails details, List<String> options, String correctOption) {
        super(details, requireOptionCount(options, OPTION_COUNT), correctOption);
    }
}
