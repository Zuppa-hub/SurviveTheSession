package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import java.util.Objects;

/**
 * The result of the last answer: the question and whether it was answered correctly. An
 * expired time counts as a wrong answer.
 */
public record AnswerResult(Question question, boolean correct) {

    public AnswerResult {
        Objects.requireNonNull(question, "Question must not be null");
    }
}
