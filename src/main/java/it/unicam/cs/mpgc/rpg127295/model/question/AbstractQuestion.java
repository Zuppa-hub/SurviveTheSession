package it.unicam.cs.mpgc.rpg127295.model.question;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class holding the data shared by every question.
 * <p>
 * Equality is final and based only on the id, as required by the {@link Question} contract:
 * subclasses cannot change it.
 */
public abstract class AbstractQuestion implements Question {

    private final QuestionDetails details;

    /** @throws NullPointerException if details is null */
    protected AbstractQuestion(QuestionDetails details) {
        this.details = Objects.requireNonNull(details, "Question details must not be null");
    }

    @Override
    public final String id() {
        return details.id();
    }

    @Override
    public final Subject subject() {
        return details.subject();
    }

    @Override
    public final Difficulty difficulty() {
        return details.difficulty();
    }

    @Override
    public final String prompt() {
        return details.prompt();
    }

    @Override
    public final Optional<String> hint() {
        return details.hint();
    }

    @Override
    public final boolean equals(Object other) {
        return other instanceof Question question && id().equals(question.id());
    }

    @Override
    public final int hashCode() {
        return id().hashCode();
    }

    @Override
    public String toString() {
        return id() + ": " + prompt();
    }
}
