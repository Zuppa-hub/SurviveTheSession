package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * The rules of the game, with the values balanced in issue #12 (checked by {@code BalanceTest}):
 * <ul>
 *   <li>resistance = credits x 4, 4 or 5 for an easy, medium or hard exam;</li>
 *   <li>damage of a correct answer = 7 + knowledge / 2;</li>
 *   <li>energy lost for a mistake = 1, 2 or 2 for an easy, medium or hard exam;</li>
 *   <li>a correct answer recovers 1 concentration point;</li>
 *   <li>effects: 15 extra seconds, 4 energy points restored, boosted damage doubled.</li>
 * </ul>
 * The values per difficulty are in {@link EnumMap}s instead of a {@code switch}.
 */
public final class StandardBattleRules implements BattleRules {

    private static final int BASE_DAMAGE = 7;
    private static final int KNOWLEDGE_DIVISOR = 2;
    private static final int CONCENTRATION_PER_CORRECT_ANSWER = 1;
    private static final int EXTRA_SECONDS = 15;
    private static final int ENERGY_RESTORED = 4;
    private static final int BOOSTED_DAMAGE_MULTIPLIER = 2;

    private final Map<Difficulty, Integer> resistancePerCredit = new EnumMap<>(Map.of(
            Difficulty.EASY, 4,
            Difficulty.MEDIUM, 4,
            Difficulty.HARD, 5));

    private final Map<Difficulty, Integer> energyLoss = new EnumMap<>(Map.of(
            Difficulty.EASY, 1,
            Difficulty.MEDIUM, 2,
            Difficulty.HARD, 2));

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

    @Override
    public int extraSeconds() {
        return EXTRA_SECONDS;
    }

    @Override
    public int energyRestored() {
        return ENERGY_RESTORED;
    }

    @Override
    public int boostedDamageMultiplier() {
        return BOOSTED_DAMAGE_MULTIPLIER;
    }
}
