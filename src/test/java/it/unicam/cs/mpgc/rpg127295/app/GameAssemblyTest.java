package it.unicam.cs.mpgc.rpg127295.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.Ability;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleListener;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattle;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualScheduler;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Wires the shipped content with the engine, as the application does at startup. */
class GameAssemblyTest {

    private static final Executor DIRECT = Runnable::run;
    private static final RandomGenerator RANDOM = FakeRandom.first();

    private static GameContent content;
    private final ManualScheduler scheduler = new ManualScheduler();

    @BeforeAll
    static void loadShippedContent() {
        content = GameContent.load(new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT));
    }

    @AfterEach
    void shutDownScheduler() {
        scheduler.shutdownNow();
    }

    private GameAssembly assembly() {
        return new GameAssembly(content, scheduler, DIRECT, RANDOM);
    }

    @Test
    void aNewGameHasTheChosenProfileAndTheExamsOfTheContentInOrder() {
        Game game = assembly().newGame("Andrea", "nerd");

        assertAll(
                () -> assertEquals("Andrea", game.student().name()),
                () -> assertEquals("nerd", game.student().profile().id()),
                () -> assertEquals(content.exams().all(), game.session().exams()));
    }

    @Test
    void anExamUsesTheRealTimerAndTheAbilitiesOfTheGame() {
        Game game = assembly().newGame("Andrea", "commuter");
        Exam programming = content.exams().findByCode("PROG1").orElseThrow();

        ExamBattle battle = game.startExam(programming, new BattleListener() {
        });

        assertAll(
                () -> assertEquals(1, scheduler.activeTasks()),
                () -> assertEquals(List.of("Ripasso lampo", "Colpo di genio"),
                        battle.abilities().stream().map(Ability::name).toList()));
    }

    @Test
    void anUnknownProfileIsRejected() {
        GameAssembly assembly = assembly();

        assertThrows(IllegalArgumentException.class, () -> assembly.newGame("Andrea", "astronaut"));
    }

    @Test
    void anItemWithoutEffectStopsTheAssembly() {
        GameContent withUnknownItem = new GameContent(content.exams(), content.profiles(),
                itemCatalogOf(new Item("pen", "Penna", "Scrive")), content.questions());

        assertThrows(ContentException.class,
                () -> new GameAssembly(withUnknownItem, scheduler, DIRECT, RANDOM));
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new GameAssembly(null, scheduler, DIRECT, RANDOM)),
                () -> assertThrows(NullPointerException.class, () -> new GameAssembly(content, null, DIRECT, RANDOM)),
                () -> assertThrows(NullPointerException.class, () -> new GameAssembly(content, scheduler, null, RANDOM)),
                () -> assertThrows(NullPointerException.class, () -> new GameAssembly(content, scheduler, DIRECT, null)));
    }

    private static ItemCatalog itemCatalogOf(Item... items) {
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
}
