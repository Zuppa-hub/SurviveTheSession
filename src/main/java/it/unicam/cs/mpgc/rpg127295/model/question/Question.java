package it.unicam.cs.mpgc.rpg127295.model.question;

import java.util.Optional;

/**
 * An exam question.
 * <p>
 * Contract of the type, valid for every implementation:
 * <ul>
 *   <li>implementations are immutable;</li>
 *   <li>two questions are equal if and only if they have the same {@link #id()}, whatever
 *       their concrete type, and {@code hashCode} is consistent with this rule;</li>
 *   <li>{@link #isCorrect(String)} never throws for wrong, blank or null answers;</li>
 *   <li>{@link #accept(QuestionVisitor)} calls exactly one method of the visitor, passing
 *       the question itself.</li>
 * </ul>
 */
public interface Question {

    /** @return the identifier, unique in the whole content; never blank */
    String id();

    Subject subject();

    Difficulty difficulty();

    /** @return the text of the question; never blank */
    String prompt();

    /** @return the hint, or empty if the question has none */
    Optional<String> hint();

    /**
     * @param answer the player's answer, possibly null or blank
     * @return true if the answer is correct; false otherwise, never an exception
     */
    boolean isCorrect(String answer);

    /**
     * Runs the operation of the visitor that matches the category of this question.
     *
     * @return the result of the visitor
     */
    <R> R accept(QuestionVisitor<R> visitor);
}
