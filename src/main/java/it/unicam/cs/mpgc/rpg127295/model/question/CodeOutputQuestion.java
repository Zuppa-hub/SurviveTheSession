package it.unicam.cs.mpgc.rpg127295.model.question;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;

import java.util.List;
import java.util.Optional;

/**
 * A code snippet with four possible outputs, one of which is correct.
 * <p>
 * It respects the {@link ChoiceQuestion} contract unchanged (LSP): the prompt is still the
 * question text, the answer is still the exact text of an option, and the snippet is an
 * additional piece of data exposed by {@link #code()}.
 */
public final class CodeOutputQuestion extends AbstractChoiceQuestion {

    private static final int OUTPUT_COUNT = 4;

    private final String code;

    /**
     * @param code          the snippet to analyse; never blank
     * @param outputs       exactly four distinct, non-blank outputs, in display order
     * @param correctOutput the output the snippet prints; must be one of {@code outputs}
     * @throws NullPointerException     if a reference or an output is null
     * @throws IllegalArgumentException if the code is blank or the outputs are invalid
     */
    public CodeOutputQuestion(QuestionDetails details, String code, List<String> outputs, String correctOutput) {
        super(details, requireOptionCount(outputs, OUTPUT_COUNT), correctOutput);
        this.code = requireNonBlank(code, "Code");
    }

    /** @return the snippet to analyse, formatted as in the content file */
    public String code() {
        return code;
    }

    @Override
    public Optional<String> codeSnippet() {
        return Optional.of(code);
    }
}
