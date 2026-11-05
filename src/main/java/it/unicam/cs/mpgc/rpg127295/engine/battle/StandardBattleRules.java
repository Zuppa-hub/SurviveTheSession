package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * The rules of the game, with provisional values (final balancing in issue #12):
 * <ul>
 *   <li>resistance = credits x 2, 3 or 4 for an easy, medium or hard exam;</li>
 *   <li>damage of a correct answer = 4 + knowledge / 2;</li>
 *   <li>energy lost for a mistake = 1, 2 or 3 for an easy, medium or hard exam;</li>
 *   <li>a correct answer recovers 1 concentration point.</li>
 * </ul>
 * The values per difficulty are in {@link EnumMap}s instead of a {@code switch}.
 */
public final class StandardBattleRules implements BattleRules {

    private static final int BASE_DAMAGE = 4;
    private static final int KNOWLEDGE_DIVISOR = 2;
    private static final int CONCENTRATION_PER_CORRECT_ANSWER = 1;

    private final Map<Difficulty, Integer> resistancePerCredit = new EnumMap<>(Map.of(
            Difficulty.EASY, 2,
            Difficulty.MEDIUM, 3,
            Difficulty.HARD, 4));

    private final Map<Difficulty, Integer> energyLoss = new EnumMap<>(Map.of(
            Difficulty.EASY, 1,
            Difficulty.MEDIUM, 2,
            Difficulty.HARD, 3));

    @Override
    public int resistanceOf(Exam exam) {
        Objects.requireNonNull(exam, "Exam must not be null");
        return exam.credits() * resistancePerCredit.get(exam.difficulty());
    }

    @Override
    public int damageOf(Stats stats) {
        Objects.requireNonNull(stats, "Stats must not be null");
        return BASE_DAMAGE + stats.knowledge() / KNOWLEDGE_DIVISOR;
    }

    @Override
    public int baseEnergyLoss(Difficulty difficulty) {
        Objects.requireNonNull(difficulty, "Difficulty must not be null");
        return energyLoss.get(difficulty);
    }

    @Override
    public int concentrationPerCorrectAnswer() {
        return CONCENTRATION_PER_CORRECT_ANSWER;
    }
}
