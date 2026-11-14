package it.unicam.cs.mpgc.rpg127295.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.Ability;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleListener;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattle;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.engine.game.ExamReport;
import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveMapper;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualScheduler;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;
import it.unicam.cs.mpgc.rpg127295.ui.GameService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private static final BattleListener NO_LISTENER = new BattleListener() {
    };

    private static GameContent content;
    private final ManualScheduler scheduler = new ManualScheduler();
    private final InMemorySaveGameRepository saves = new InMemorySaveGameRepository();
    private final List<SaveGameException> saveFailures = new ArrayList<>();

    @BeforeAll
    static void loadShippedContent() {
        content = GameContent.load(new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT));
    }

    @AfterEach
    void shutDownScheduler() {
        scheduler.shutdownNow();
    }

    private GameAssembly assembly() {
        return new GameAssembly(content, scheduler, DIRECT, RANDOM, saves, saveFailures::add);
    }

    @Test
    void aNewGameHasTheChosenProfileAndTheExamsOfTheContentInOrder() {
        Game game = assembly().newGame("Andrea", "nerd", "andrea");

        assertAll(
                () -> assertEquals("Andrea", game.student().name()),
                () -> assertEquals("nerd", game.student().profile().id()),
                () -> assertEquals(content.exams().all(), game.session().exams()));
    }

    @Test
    void anExamUsesTheRealTimerAndTheAbilitiesOfTheGame() {
        Game game = assembly().newGame("Andrea", "commuter", "andrea");
        Exam programming = content.exams().findByCode("PROG1").orElseThrow();

        ExamBattle battle = game.startExam(programming, NO_LISTENER);

        assertAll(
                () -> assertEquals(1, scheduler.activeTasks()),
                () -> assertEquals(List.of("Ripasso lampo", "Colpo di genio"),
                        battle.abilities().stream().map(Ability::name).toList()));
    }

    @Test
    void theGameIsSavedAfterEveryExam() {
        Game game = assembly().newGame("Andrea", "nerd", "andrea");
        Exam programming = content.exams().findByCode("PROG1").orElseThrow();
        boolean savedBeforeTheExam = saves.load("andrea").isPresent();

        game.startExam(programming, NO_LISTENER).retire();

        assertAll(
                () -> assertFalse(savedBeforeTheExam),
                () -> assertEquals("Andrea", saves.load("andrea").orElseThrow().studentName()),
                () -> assertEquals(List.of("andrea"), assembly().savedGames()));
    }

    @Test
    void aLoadedGameRestoresTheStudentAndKeepsSavingInTheSameSlot() {
        Student student = assembly().newGame("Andrea", "nerd", "andrea").student();
        student.transcript().record(content.exams().findByCode("PROG1").orElseThrow(), Grade.of(28));
        student.inventory().add(content.items().findById("coffee").orElseThrow(), 2);
        saves.save("andrea", new SaveMapper(content.profiles(), content.items(), content.exams()).toData(student));

        Game loaded = assembly().loadGame("andrea").orElseThrow();
        loaded.startExam(content.exams().findByCode("ASD1").orElseThrow(), NO_LISTENER).retire();

        assertAll(
                () -> assertEquals(student.transcript().entries(), loaded.student().transcript().entries()),
                () -> assertEquals(student.inventory().items(), loaded.student().inventory().items()),
                () -> assertEquals(Map.of("PROG1", 28), saves.load("andrea").orElseThrow().transcript()));
    }

    @Test
    void aFailedAutomaticSaveIsReportedAndTheGameGoesOn() {
        SaveGameException diskFull = new SaveGameException("Disco pieno");
        GameAssembly assembly = new GameAssembly(content, scheduler, DIRECT, RANDOM,
                new FailingSaveGameRepository(diskFull), saveFailures::add);
        Game game = assembly.newGame("Andrea", "nerd", "andrea");
        List<ExamReport> reports = new ArrayList<>();
        game.addListener(reports::add);
        Exam programming = content.exams().findByCode("PROG1").orElseThrow();

        game.startExam(programming, NO_LISTENER).retire();
        game.startExam(programming, NO_LISTENER).retire();

        assertAll(
                () -> assertEquals(List.of(diskFull, diskFull), saveFailures),
                () -> assertEquals(2, reports.size()));
    }

    @Test
    void loadingAMissingSlotGivesNothing() {
        assertEquals(Optional.empty(), assembly().loadGame("never-saved"));
    }

    @Test
    void aNewGameNeedsAValidSlotName() {
        GameAssembly assembly = assembly();

        assertThrows(IllegalArgumentException.class, () -> assembly.newGame("Andrea", "nerd", "../andrea"));
    }

    @Test
    void isTheGameServiceOfTheUserInterface() {
        GameService service = assembly();

        assertEquals(content.profiles().all(), service.profiles());
    }

    @Test
    void anUnknownProfileIsRejected() {
        GameAssembly assembly = assembly();

        assertThrows(IllegalArgumentException.class, () -> assembly.newGame("Andrea", "astronaut", "andrea"));
    }

    @Test
    void anItemWithoutEffectStopsTheAssembly() {
        GameContent withUnknownItem = new GameContent(content.exams(), content.profiles(),
                itemCatalogOf(new Item("pen", "Penna", "Scrive")), content.questions());

        assertThrows(ContentException.class,
                () -> new GameAssembly(withUnknownItem, scheduler, DIRECT, RANDOM, saves, saveFailures::add));
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new GameAssembly(null, scheduler, DIRECT, RANDOM, saves, saveFailures::add)),
                () -> assertThrows(NullPointerException.class,
                        () -> new GameAssembly(content, null, DIRECT, RANDOM, saves, saveFailures::add)),
                () -> assertThrows(NullPointerException.class,
                        () -> new GameAssembly(content, scheduler, null, RANDOM, saves, saveFailures::add)),
                () -> assertThrows(NullPointerException.class,
                        () -> new GameAssembly(content, scheduler, DIRECT, null, saves, saveFailures::add)),
                () -> assertThrows(NullPointerException.class,
                        () -> new GameAssembly(content, scheduler, DIRECT, RANDOM, null, saveFailures::add)),
                () -> assertThrows(NullPointerException.class,
                        () -> new GameAssembly(content, scheduler, DIRECT, RANDOM, saves, null)));
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
