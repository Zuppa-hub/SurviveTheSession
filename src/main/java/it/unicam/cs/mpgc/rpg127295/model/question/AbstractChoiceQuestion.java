package it.unicam.cs.mpgc.rpg127295.model.question;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Base class for questions with options. It owns the validation of the options and the
 * answer check, which are final so that every choice question behaves the same way (LSP).
 */
public abstract class AbstractChoiceQuestion extends AbstractQuestion implements ChoiceQuestion {

    private static final int MIN_OPTIONS = 2;

    private final List<String> options;
    private final List<String> wrongOptions;
    private final String correctOption;

    /**
     * @param options       at least 2 distinct, non-blank options, in display order
     * @param correctOption the correct option; must be one of {@code options}
     * @throws NullPointerException     if a reference or an option is null
     * @throws IllegalArgumentException if the options are invalid or do not contain the correct one
     */
    protected AbstractChoiceQuestion(QuestionDetails details, List<String> options, String correctOption) {
        super(details);
        this.options = validOptions(options);
        this.correctOption = Objects.requireNonNull(correctOption, "Correct option must not be null");
        if (!this.options.contains(correctOption)) {
            throw new IllegalArgumentException("Correct option is not among the options: " + correctOption);
        }
        this.wrongOptions = this.options.stream()
                .filter(option -> !option.equals(correctOption))
                .toList();
    }

    /**
     * Checks the number of options, for subclasses that require an exact count.
     *
     * @return the same list, to be passed to the constructor
     * @throws IllegalArgumentException if the list does not have exactly {@code count} options
     */
    protected static List<String> requireOptionCount(List<String> options, int count) {
        Objects.requireNonNull(options, "Options must not be null");
        if (options.size() != count) {
            throw new IllegalArgumentException(
                    "Expected exactly " + count + " options, found " + options.size());
        }
        return options;
    }

    @Override
    public final List<String> options() {
        return options;
    }

    @Override
    public final List<String> wrongOptions() {
        return wrongOptions;
    }

    @Override
    public final boolean isCorrect(String answer) {
        return correctOption.equals(answer);
    }

    @Override
    public final <R> R accept(QuestionVisitor<R> visitor) {
        return visitor.visitChoice(this);
    }

    private static List<String> validOptions(List<String> options) {
        List<String> copy = List.copyOf(Objects.requireNonNull(options, "Options must not be null"));
        if (copy.size() < MIN_OPTIONS) {
            throw new IllegalArgumentException("At least " + MIN_OPTIONS + " options are required");
        }
        copy.forEach(option -> requireNonBlank(option, "Option"));
        if (new HashSet<>(copy).size() != copy.size()) {
            throw new IllegalArgumentException("Options must be distinct: " + copy);
        }
        return copy;
    }
}
