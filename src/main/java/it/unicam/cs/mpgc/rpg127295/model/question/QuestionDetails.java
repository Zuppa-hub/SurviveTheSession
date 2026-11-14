package it.unicam.cs.mpgc.rpg127295.model.question;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;

import java.util.Objects;
import java.util.Optional;

/**
 * The data shared by every kind of question, grouped in one parameter object so that question
 * constructors stay short. Immutable value object.
 *
 * @param id         unique identifier in the whole content; never blank
 * @param subject    the subject the question belongs to
 * @param difficulty how hard the question is
 * @param prompt     the text of the question; never blank
 * @param hint       an optional hint; when present it is never blank
 */
public record QuestionDetails(String id, Subject subject, Difficulty difficulty, String prompt,
                              Optional<String> hint) {

    /**
     * @throws IllegalArgumentException if id, prompt or a present hint is blank
     * @throws NullPointerException     if subject, difficulty or hint is null
     */
    public QuestionDetails {
        requireNonBlank(id, "Question id");
        Objects.requireNonNull(subject, "Question subject must not be null");
        Objects.requireNonNull(difficulty, "Question difficulty must not be null");
        requireNonBlank(prompt, "Question prompt");
        Objects.requireNonNull(hint, "Question hint must not be null; use Optional.empty()");
        hint.ifPresent(text -> requireNonBlank(text, "Question hint"));
    }

    /** @throws IllegalArgumentException if the hint is null or blank */
    public static QuestionDetails withHint(String id, Subject subject, Difficulty difficulty,
                                           String prompt, String hint) {
        return new QuestionDetails(id, subject, difficulty, prompt,
                Optional.of(requireNonBlank(hint, "Question hint")));
    }

    public static QuestionDetails withoutHint(String id, Subject subject, Difficulty difficulty,
                                              String prompt) {
        return new QuestionDetails(id, subject, difficulty, prompt, Optional.empty());
    }
}
