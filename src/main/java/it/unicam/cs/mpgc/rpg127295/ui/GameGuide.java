package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.battle.Ability;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleRules;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.game.ProgressionRules;
import it.unicam.cs.mpgc.rpg127295.engine.game.RewardPolicy;
import it.unicam.cs.mpgc.rpg127295.engine.game.StarterKit;
import it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamSession;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * The rules and the content of the game, as the "how to play" pages and the empty states need
 * them. The composition root passes the same objects the games use, so a number shown to the
 * player is always the one the game applies: what the rules do not expose directly (the share
 * needed to pass, the first grade with an item) is found by asking them.
 *
 * @param strategies one for every type of exam; copied
 */
public record GameGuide(ExamSession session, List<StudentProfile> profiles, StarterKit starterKit,
                        BattleRules battleRules, Map<ExamType, ExamStrategy> strategies, List<Ability> abilities,
                        List<Item> items, ProgressionRules progression, RewardPolicy rewards, GradeCalculator grades) {

    private static final int ANSWERS_FOR_A_PERCENTAGE = 100;

    /** @throws IllegalArgumentException if a type of exam has no strategy */
    public GameGuide {
        Objects.requireNonNull(session, "Session must not be null");
        profiles = List.copyOf(Objects.requireNonNull(profiles, "Profiles must not be null"));
        Objects.requireNonNull(starterKit, "Starter kit must not be null");
        Objects.requireNonNull(battleRules, "Battle rules must not be null");
        strategies = copyOfEveryType(Objects.requireNonNull(strategies, "Strategies must not be null"));
        abilities = List.copyOf(Objects.requireNonNull(abilities, "Abilities must not be null"));
        items = List.copyOf(Objects.requireNonNull(items, "Items must not be null"));
        Objects.requireNonNull(progression, "Progression rules must not be null");
        Objects.requireNonNull(rewards, "Reward policy must not be null");
        Objects.requireNonNull(grades, "Grade calculator must not be null");
    }

    /**
     * @return the lowest share of correct answers, out of 100, that passes an exam without hints
     * @throws IllegalStateException if no share passes, which the calculator contract forbids
     */
    public int passingPercentage() {
        return IntStream.rangeClosed(0, ANSWERS_FOR_A_PERCENTAGE)
                .filter(correct -> passes(correct, ANSWERS_FOR_A_PERCENTAGE - correct))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No share of correct answers passes"));
    }

    /** @return the lowest grade rewarded with at least one item in the first exam, if any */
    public Optional<Grade> lowestGradeWithAnItem() {
        return Grade.all().stream()
                .filter(grade -> !rewards.rewardFor(session.exams().getFirst(), grade).items().isEmpty())
                .findFirst();
    }

    /** @return the energy lost for a mistake in an exam of this difficulty and type */
    public int energyLoss(Difficulty difficulty, ExamType type) {
        return battleRules.baseEnergyLoss(difficulty) * strategyOf(type).mistakeMultiplier();
    }

    public OptionalInt secondsPerQuestion(ExamType type) {
        return strategyOf(type).secondsPerQuestion();
    }

    public ExamStrategy strategyOf(ExamType type) {
        return strategies.get(Objects.requireNonNull(type, "Exam type must not be null"));
    }

    private boolean passes(int correct, int wrong) {
        return grades.calculate(new Performance(correct, wrong, 0)).isPresent();
    }

    private static Map<ExamType, ExamStrategy> copyOfEveryType(Map<ExamType, ExamStrategy> strategies) {
        boolean everyType = Arrays.stream(ExamType.values()).allMatch(strategies::containsKey);
        if (!everyType) {
            throw new IllegalArgumentException("Every type of exam needs a strategy: " + strategies.keySet());
        }
        return Collections.unmodifiableMap(new EnumMap<>(strategies));
    }
}
