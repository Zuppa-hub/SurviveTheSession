package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;

/**
 * Entry point of the application. Content is loaded first: if it is invalid the game does not
 * start (fail fast).
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        try {
            GameContent content = GameContent.load(
                    new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT));
            GameEffects.of(content.items());
            System.out.println("RPG avviato: " + content.exams().all().size() + " esami caricati");
        } catch (ContentException error) {
            System.err.println("Impossibile avviare il gioco, contenuti non validi:");
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }
}
