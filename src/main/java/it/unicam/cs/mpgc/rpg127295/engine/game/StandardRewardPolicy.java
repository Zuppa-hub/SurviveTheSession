package it.unicam.cs.mpgc.rpg127295.engine.game;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Provisional rewards (final balancing in issue #12):
 * <ul>
 *   <li>experience = credits x (grade - 15), honors counting as 30;</li>
 *   <li>items: none for 18-23, one for 24-27, two for 28-30, three with honors, each chosen at
 *       random among the items of the catalog.</li>
 * </ul>
 */
public final class StandardRewardPolicy implements RewardPolicy {

    private static final int GRADE_WITHOUT_EXPERIENCE = 15;
    private static final int ONE_ITEM_FROM = 24;
    private static final int TWO_ITEMS_FROM = 28;
    private static final int ITEMS_WITH_HONORS = 3;

    private final ItemCatalog items;
    private final RandomGenerator random;

    public StandardRewardPolicy(ItemCatalog items, RandomGenerator random) {
        this.items = Objects.requireNonNull(items, "Item catalog must not be null");
        this.random = Objects.requireNonNull(random, "Random generator must not be null");
    }

    @Override
    public Reward rewardFor(Exam exam, Grade grade) {
        Objects.requireNonNull(exam, "Exam must not be null");
        Objects.requireNonNull(grade, "Grade must not be null");
        int experience = exam.credits() * (grade.valueForAverage() - GRADE_WITHOUT_EXPERIENCE);
        return new Reward(experience, randomItems(itemCountFor(grade)));
    }

    private static int itemCountFor(Grade grade) {
        if (grade.isWithHonors()) {
            return ITEMS_WITH_HONORS;
        }
        if (grade.value() >= TWO_ITEMS_FROM) {
            return 2;
        }
        return grade.value() >= ONE_ITEM_FROM ? 1 : 0;
    }

    private Map<Item, Integer> randomItems(int count) {
        List<Item> available = items.all();
        Map<Item, Integer> chosen = new HashMap<>();
        if (available.isEmpty()) {
            return chosen;
        }
        for (int i = 0; i < count; i++) {
            chosen.merge(available.get(random.nextInt(available.size())), 1, Integer::sum);
        }
        return chosen;
    }
}
