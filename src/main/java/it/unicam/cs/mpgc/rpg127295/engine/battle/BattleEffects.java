package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The effects available in the battles: the effect of each item and the abilities of the
 * student. Built by the application.
 *
 * @param itemEffects the effect of each item; copied
 * @param abilities   the abilities, with distinct names; copied
 */
public record BattleEffects(Map<Item, BattleEffect> itemEffects, List<Ability> abilities) {

    /** @throws IllegalArgumentException if two abilities have the same name */
    public BattleEffects {
        itemEffects = Map.copyOf(Objects.requireNonNull(itemEffects, "Item effects must not be null"));
        abilities = List.copyOf(Objects.requireNonNull(abilities, "Abilities must not be null"));
        if (new HashSet<>(abilities.stream().map(Ability::name).toList()).size() != abilities.size()) {
            throw new IllegalArgumentException("Abilities must have distinct names: " + abilities);
        }
    }

    /** @return no item effects and no abilities */
    public static BattleEffects none() {
        return new BattleEffects(Map.of(), List.of());
    }

    public Optional<BattleEffect> effectOf(Item item) {
        return Optional.ofNullable(itemEffects.get(item));
    }

    public boolean hasAbility(Ability ability) {
        return abilities.contains(ability);
    }
}
