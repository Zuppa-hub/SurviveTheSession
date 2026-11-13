package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.battle.Ability;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattle;
import it.unicam.cs.mpgc.rpg127295.model.item.Inventory;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * An item or an ability as a button of the exam screen. Whether it can be used is asked to the
 * battle ({@link ExamBattle#canUse}), so the screen holds no rule about effects.
 *
 * @param label       the text of the button
 * @param description the tooltip
 * @param enabled     whether the battle allows it now
 * @param use         uses the item or the ability
 */
public record BattleAction(String label, String description, boolean enabled, Runnable use) {

    public BattleAction {
        Objects.requireNonNull(label, "Label must not be null");
        Objects.requireNonNull(description, "Description must not be null");
        Objects.requireNonNull(use, "Use must not be null");
    }

    /** @return the owned items, by name, followed by the abilities of the battle */
    public static List<BattleAction> of(ExamBattle battle, Inventory inventory) {
        Objects.requireNonNull(battle, "Battle must not be null");
        Objects.requireNonNull(inventory, "Inventory must not be null");
        return Stream.concat(itemActions(battle, inventory), abilityActions(battle)).toList();
    }

    private static Stream<BattleAction> itemActions(ExamBattle battle, Inventory inventory) {
        return inventory.items().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Item::name)))
                .map(entry -> itemAction(battle, entry.getKey(), entry.getValue()));
    }

    private static BattleAction itemAction(ExamBattle battle, Item item, int quantity) {
        return new BattleAction(item.name() + " × " + quantity, item.description(), battle.canUse(item),
                () -> battle.useItem(item));
    }

    private static Stream<BattleAction> abilityActions(ExamBattle battle) {
        return battle.abilities().stream()
                .map(ability -> abilityAction(battle, ability));
    }

    private static BattleAction abilityAction(ExamBattle battle, Ability ability) {
        int cost = ability.concentrationCost();
        return new BattleAction(ability.name() + " · " + cost + " Concentrazione",
                "Costa " + cost + " di Concentrazione", battle.canUse(ability), () -> battle.useAbility(ability));
    }
}
