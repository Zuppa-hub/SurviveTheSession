package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.Objects;
import java.util.Optional;

/**
 * The end of a battle. Invariant: the grade is present if and only if the exam is passed.
 *
 * @param result      how the battle ended
 * @param grade       the grade of a passed exam; empty otherwise
 * @param performance the answers given in the battle
 */
public record BattleOutcome(BattleResult result, Optional<Grade> grade, Performance performance) {

    public BattleOutcome {
        Objects.requireNonNull(result, "Result must not be null");
        Objects.requireNonNull(grade, "Grade must not be null");
        Objects.requireNonNull(performance, "Performance must not be null");
        if ((result == BattleResult.PASSED) != grade.isPresent()) {
            throw new IllegalArgumentException("A grade is required if and only if the exam is passed: "
                    + result + ", " + grade);
        }
    }

    public static BattleOutcome passed(Grade grade, Performance performance) {
        return new BattleOutcome(BattleResult.PASSED, Optional.of(grade), performance);
    }

    public static BattleOutcome failed(Performance performance) {
        return new BattleOutcome(BattleResult.FAILED, Optional.empty(), performance);
    }

    public static BattleOutcome retired(Performance performance) {
        return new BattleOutcome(BattleResult.RETIRED, Optional.empty(), performance);
    }
}
