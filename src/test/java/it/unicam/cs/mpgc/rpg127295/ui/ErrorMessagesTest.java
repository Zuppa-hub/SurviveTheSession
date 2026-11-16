package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** The dialogs speak Italian: the English message of an exception is for the log only. */
class ErrorMessagesTest {

    @Test
    void anUnreadableSaveNamesTheSlotInItalian() {
        ErrorMessages message = ErrorMessages.loadFailed("test-3");

        assertAll(
                () -> assertEquals("Impossibile caricare la partita", message.header()),
                () -> assertEquals("Il salvataggio \"test-3\" è danneggiato oppure è stato creato da una "
                        + "versione del gioco non compatibile.", message.text()));
    }

    @Test
    void aMissingSaveNamesTheSlot() {
        assertEquals("Il salvataggio \"test-3\" non esiste più.", ErrorMessages.saveMissing("test-3").text());
    }

    @Test
    void aFailedSaveTellsWhatToCheck() {
        ErrorMessages message = ErrorMessages.saveFailed();

        assertAll(
                () -> assertEquals("La partita non è stata salvata, ma puoi continuare a giocare.", message.header()),
                () -> assertEquals("Il gioco riproverà alla fine del prossimo esame. Controlla che ci sia spazio "
                        + "sul disco e che la cartella dei salvataggi sia scrivibile.", message.text()));
    }

    @Test
    void unreadableSavesTellThatANewGameIsStillPossible() {
        ErrorMessages message = ErrorMessages.savesUnreadable();

        assertAll(
                () -> assertEquals("Impossibile leggere le partite salvate", message.header()),
                () -> assertEquals("La cartella dei salvataggi non è leggibile: controlla i permessi. Puoi "
                        + "iniziare una nuova partita, ma potrebbe non essere salvata.", message.text()));
    }

    @Test
    void invalidContentStopsTheGame() {
        assertEquals("I file dei contenuti del gioco (esami, domande, oggetti) non sono validi: il gioco "
                + "verrà chiuso.", ErrorMessages.contentInvalid().text());
    }

    @Test
    void rejectsMissingTexts() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ErrorMessages.loadFailed(null)),
                () -> assertThrows(NullPointerException.class, () -> new ErrorMessages(null, "text")),
                () -> assertThrows(NullPointerException.class, () -> new ErrorMessages("header", null)));
    }
}
