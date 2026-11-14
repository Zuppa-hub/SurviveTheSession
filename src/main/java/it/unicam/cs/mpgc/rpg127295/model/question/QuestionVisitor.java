package it.unicam.cs.mpgc.rpg127295.model.question;

/**
 * An operation that depends on the category of a question, chosen by
 * {@link Question#accept(QuestionVisitor)} instead of {@code instanceof} (Visitor pattern).
 * <p>
 * There is one method per category, not per concrete class: a new type of choice question
 * needs no change here, while a new category needs a new method.
 *
 * @param <R> the result of the operation
 */
public interface QuestionVisitor<R> {

    R visitChoice(ChoiceQuestion question);

    R visitFillIn(FillInQuestion question);
}
