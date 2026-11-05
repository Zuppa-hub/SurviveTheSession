package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.battle.AbilityTest.NO_EFFECT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BattleEffectsTest {

    private static final Item COFFEE = new Item("coffee", "Caffè", "Più tempo");
    private static final Ability QUICK_REVIEW = new Ability("Ripasso lampo", 2, NO_EFFECT);

    @Test
    void findsTheEffectOfAnItem() {
        BattleEffects effects = new BattleEffects(Map.of(COFFEE, NO_EFFECT), List.of());

        assertAll(
                () -> assertEquals(Optional.of(NO_EFFECT), effects.effectOf(COFFEE)),
                () -> assertEquals(Optional.empty(), effects.effectOf(new Item("notes", "Appunti", "50/50"))));
    }

    @Test
    void knowsItsAbilities() {
        BattleEffects effects = new BattleEffects(Map.of(), List.of(QUICK_REVIEW));

        assertAll(
                () -> assertTrue(effects.hasAbility(QUICK_REVIEW)),
                () -> assertFalse(effects.hasAbility(new Ability("Colpo di genio", 3, NO_EFFECT))));
    }

    @Test
    void noneHasNoEffectsAndNoAbilities() {
        assertAll(
                () -> assertTrue(BattleEffects.none().itemEffects().isEmpty()),
                () -> assertTrue(BattleEffects.none().abilities().isEmpty()));
    }

    @Test
    void rejectsTwoAbilitiesWithTheSameName() {
        List<Ability> abilities = List.of(QUICK_REVIEW, new Ability("Ripasso lampo", 3, NO_EFFECT));

        assertThrows(IllegalArgumentException.class, () -> new BattleEffects(Map.of(), abilities));
    }

    @Test
    void isNotAffectedByChangesToTheOriginalCollections() {
        Map<Item, BattleEffect> items = new HashMap<>(Map.of(COFFEE, NO_EFFECT));
        BattleEffects effects = new BattleEffects(items, List.of());

        items.clear();

        assertTrue(effects.effectOf(COFFEE).isPresent());
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new BattleEffects(null, List.of())),
                () -> assertThrows(NullPointerException.class, () -> new BattleEffects(Map.of(), null)));
    }
}
