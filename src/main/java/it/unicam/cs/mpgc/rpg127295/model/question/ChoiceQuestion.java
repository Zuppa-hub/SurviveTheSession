package it.unicam.cs.mpgc.rpg127295.model.question;

import java.util.List;

/**
 * A question answered by choosing one of its options (ISP: only these questions have options).
 * <p>
 * In addition to the {@link Question} contract: the answer is the exact text of an option,
 * and exactly one option is correct. The 50/50 (Notes) applies only when
 * {@code wrongOptions().size() >= 3}, so that at least one wrong option is left after two are
 * removed.
 */
public interface ChoiceQuestion extends Question {

    /** @return the options in display order: unmodifiable, at least 2, distinct, never blank */
    List<String> options();

    /** @return the options except the correct one, in display order; unmodifiable */
    List<String> wrongOptions();
}
