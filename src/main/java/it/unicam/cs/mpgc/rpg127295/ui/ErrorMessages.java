package it.unicam.cs.mpgc.rpg127295.ui;

import java.util.Objects;

/**
 * The texts of the error dialogs, in Italian. Exceptions keep their English message, written
 * for the developer: it goes to the log, never to the player.
 *
 * @param header the first line of the dialog
 * @param text   what happened and what to do
 */
public record ErrorMessages(String header, String text) {

    public ErrorMessages {
        Objects.requireNonNull(header, "Header must not be null");
        Objects.requireNonNull(text, "Text must not be null");
    }

    public static ErrorMessages loadFailed(String slot) {
        return new ErrorMessages("Impossibile caricare la partita", "Il salvataggio " + quoted(slot)
                + " è danneggiato oppure è stato creato da una versione del gioco non compatibile.");
    }

    public static ErrorMessages saveMissing(String slot) {
        return new ErrorMessages("Partita non trovata", "Il salvataggio " + quoted(slot) + " non esiste più.");
    }

    public static ErrorMessages saveFailed() {
        return new ErrorMessages("La partita non è stata salvata, ma puoi continuare a giocare.",
                "Il gioco riproverà alla fine del prossimo esame. Controlla che ci sia spazio sul disco e "
                        + "che la cartella dei salvataggi sia scrivibile.");
    }

    public static ErrorMessages contentInvalid() {
        return new ErrorMessages("Impossibile avviare il gioco",
                "I file dei contenuti del gioco (esami, domande, oggetti) non sono validi: il gioco verrà chiuso.");
    }

    private static String quoted(String slot) {
        return "\"" + Objects.requireNonNull(slot, "Slot must not be null") + "\"";
    }
}
