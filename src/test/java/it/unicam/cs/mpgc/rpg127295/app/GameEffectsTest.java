package it.unicam.cs.mpgc.rpg127295.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.battle.Ability;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffects;
import it.unicam.cs.mpgc.rpg127295.engine.effect.DoubleDamageEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.HintEffect;
import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GameEffectsTest {

    private static ItemCatalog catalogOf(Item... items) {
        return new ItemCatalog() {
            @Override
            public List<Item> all() {
                return List.of(items);
            }

            @Override
            public Optional<Item> findById(String id) {
                return all().stream().filter(item -> item.id().equals(id)).findFirst();
            }
        };
    }

    @Test
    void everyShippedItemHasAnEffect() {
        ItemCatalog items = GameContent.load(
                new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT)).items();

        BattleEffects effects = GameEffects.of(items);

        items.all().forEach(item -> assertTrue(effects.effectOf(item).isPresent(), item.id()));
    }

    @Test
    void anItemWithoutEffectStopsTheGame() {
        ItemCatalog items = catalogOf(new Item("pen", "Penna", "Scrive"));

        ContentException error = assertThrows(ContentException.class, () -> GameEffects.of(items));
        assertTrue(error.getMessage().contains("pen"), error.getMessage());
    }

    @Test
    void summaryAndQuickReviewShareTheHintEffect() {
        Item summary = new Item("summary", "Riassunto", "Indizio");

        BattleEffects effects = GameEffects.of(catalogOf(summary));

        assertAll(
                () -> assertInstanceOf(HintEffect.class, effects.effectOf(summary).orElseThrow()),
                () -> assertInstanceOf(HintEffect.class, effects.abilities().getFirst().effect()));
    }

    @Test
    void theAbilitiesAreQuickReviewAndStrokeOfGenius() {
        List<Ability> abilities = GameEffects.of(catalogOf()).abilities();

        assertAll(
                () -> assertEquals(List.of("Ripasso lampo", "Colpo di genio"),
                        abilities.stream().map(Ability::name).toList()),
                () -> assertEquals(List.of(2, 3), abilities.stream().map(Ability::concentrationCost).toList()),
                () -> assertInstanceOf(DoubleDamageEffect.class, abilities.getLast().effect()));
    }
}
