package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.engine.battle.Ability;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffect;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffects;
import it.unicam.cs.mpgc.rpg127295.engine.effect.DoubleDamageEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.EnergyRestoreEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.ExtraTimeEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.FiftyFiftyEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.HintEffect;
import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Part of the composition root: associates each item of the content with its effect, by id,
 * and defines the abilities. A new item with an existing effect is one entry in the map; an
 * item of the content without an effect stops the game at startup (fail fast).
 */
public final class GameEffects {

    private static final BattleEffect SHOW_HINT = new HintEffect();

    private static final Map<String, BattleEffect> EFFECTS_BY_ITEM_ID = Map.of(
            "coffee", new ExtraTimeEffect(),
            "notes", new FiftyFiftyEffect(),
            "summary", SHOW_HINT,
            "energy-drink", new EnergyRestoreEffect());

    private static final List<Ability> ABILITIES = List.of(
            new Ability("Ripasso lampo", 2, SHOW_HINT),
            new Ability("Colpo di genio", 3, new DoubleDamageEffect()));

    private GameEffects() {
    }

    /** @throws ContentException if an item of the catalog has no effect */
    public static BattleEffects of(ItemCatalog items) {
        Map<Item, BattleEffect> itemEffects = items.all().stream()
                .collect(Collectors.toMap(Function.identity(), GameEffects::effectOf));
        return new BattleEffects(itemEffects, ABILITIES);
    }

    private static BattleEffect effectOf(Item item) {
        BattleEffect effect = EFFECTS_BY_ITEM_ID.get(item.id());
        if (effect == null) {
            throw new ContentException("No effect for item '" + item.id() + "' in items.xml");
        }
        return effect;
    }
}
