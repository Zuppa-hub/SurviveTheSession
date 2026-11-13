package it.unicam.cs.mpgc.rpg127295.model.question;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A question answered with a short free text.
 * <p>
 * Answers are compared after normalization: surrounding whitespace removed, inner whitespace
 * collapsed to a single space, letters in lower case. Punctuation and accents are kept.
 * Several equivalent answers can be accepted, such as "LIFO" and "Last In First Out".
 */
public final class FillInQuestion extends AbstractQuestion {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private final Set<String> acceptedAnswers;

    /**
     * @param acceptedAnswers at least one non-blank answer; answers equal after normalization
     *                        count once
     * @throws NullPointerException     if a reference or an answer is null
     * @throws IllegalArgumentException if no answer is given or an answer is blank
     */
    public FillInQuestion(QuestionDetails details, Set<String> acceptedAnswers) {
        super(details);
        this.acceptedAnswers = normalizedAnswers(acceptedAnswers);
    }

    @Override
    public boolean isCorrect(String answer) {
        return answer != null && acceptedAnswers.contains(normalize(answer));
    }

    @Override
    public <R> R accept(QuestionVisitor<R> visitor) {
        return visitor.visitFillIn(this);
    }

    private static Set<String> normalizedAnswers(Set<String> answers) {
        Objects.requireNonNull(answers, "Accepted answers must not be null");
        if (answers.isEmpty()) {
            throw new IllegalArgumentException("At least one accepted answer is required");
        }
        return answers.stream()
                .map(FillInQuestion::validAnswer)
                .map(FillInQuestion::normalize)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static String validAnswer(String answer) {
        Objects.requireNonNull(answer, "Accepted answer must not be null");
        return requireNonBlank(answer, "Accepted answer");
    }

    private static String normalize(String text) {
        return WHITESPACE.matcher(text.strip()).replaceAll(" ").toLowerCase(Locale.ROOT);
    }
}
