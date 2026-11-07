package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;
import java.util.concurrent.ScheduledExecutorService;
import java.util.random.RandomGenerator;

/**
 * Entry point of the application. Content is loaded and the engine is assembled first: if
 * anything is invalid the game does not start (fail fast).
 * <p>
 * Until the user interface exists (issue #10) the timer notifications are delivered directly;
 * no battle is started here, and the interface will pass {@code Platform::runLater} instead.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = GameAssembly.newTimerScheduler();
        try {
            GameContent content = GameContent.load(
                    new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT));
            new GameAssembly(content, scheduler, Runnable::run, RandomGenerator.getDefault());
            System.out.println("RPG avviato: " + content.exams().all().size() + " esami caricati");
        } catch (ContentException error) {
            System.err.println("Impossibile avviare il gioco, contenuti non validi:");
            System.err.println(error.getMessage());
            System.exit(1);
        } finally {
            scheduler.shutdownNow();
        }
    }
}
