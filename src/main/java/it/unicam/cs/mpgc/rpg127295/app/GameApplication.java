package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;
import it.unicam.cs.mpgc.rpg127295.persistence.save.JsonSaveGameRepository;
import it.unicam.cs.mpgc.rpg127295.ui.Alerts;
import it.unicam.cs.mpgc.rpg127295.ui.ViewNavigator;
import java.nio.file.Path;
import java.util.concurrent.ScheduledExecutorService;
import java.util.random.RandomGenerator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * The JavaFX application, part of the composition root: it loads the content, connects the
 * engine to the interface and closes what it opened.
 * <ul>
 *   <li>The timer notifications are delivered with {@code Platform::runLater}, so the battle is
 *       always changed on the JavaFX Application Thread.</li>
 *   <li>A failed automatic save shows a warning and the game goes on.</li>
 *   <li>{@link #stop()} shuts down the scheduler of the timer, which belongs to the
 *       application.</li>
 * </ul>
 */
public final class GameApplication extends Application {

    private static final String TITLE = "Sopravvivi alla Sessione";
    private static final Path SAVES_DIRECTORY =
            Path.of(System.getProperty("user.home"), ".sopravvivi-alla-sessione", "saves");

    private final ScheduledExecutorService scheduler = GameAssembly.newTimerScheduler();

    @Override
    public void start(Stage stage) {
        try {
            GameContent content = GameContent.load(
                    new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT));
            GameAssembly games = new GameAssembly(content, scheduler, Platform::runLater,
                    RandomGenerator.getDefault(), new JsonSaveGameRepository(SAVES_DIRECTORY), Alerts::saveFailed);
            stage.setTitle(TITLE);
            new ViewNavigator(stage, games).showMenu();
            stage.show();
        } catch (ContentException error) {
            Alerts.error("Impossibile avviare il gioco: contenuti non validi", error.getMessage());
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
    }
}
