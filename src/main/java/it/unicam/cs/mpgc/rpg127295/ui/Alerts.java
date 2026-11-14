package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import javafx.scene.control.Alert;

/**
 * The dialogs of the game. To be called on the JavaFX Application Thread. They show the
 * Italian texts of {@link ErrorMessages}; the exception, in English, goes to the log.
 */
public final class Alerts {

    private static final Logger LOG = System.getLogger(Alerts.class.getName());

    private Alerts() {
    }

    /**
     * Warns that an automatic save failed, without blocking the game: the dialog does not wait
     * for the player.
     */
    public static void saveFailed(SaveGameException failure) {
        LOG.log(Level.WARNING, "Automatic save failed", failure);
        alert(Alert.AlertType.WARNING, "Salvataggio non riuscito", ErrorMessages.saveFailed()).show();
    }

    public static void loadFailed(String slot, SaveGameException failure) {
        LOG.log(Level.WARNING, "Cannot load slot " + slot, failure);
        error(ErrorMessages.loadFailed(slot));
    }

    /** Tells the player that the game cannot start; the cause goes to the log. */
    public static void startFailed(RuntimeException failure) {
        LOG.log(Level.ERROR, "Invalid content", failure);
        error(ErrorMessages.contentInvalid());
    }

    public static void error(ErrorMessages message) {
        alert(Alert.AlertType.ERROR, "Errore", message).showAndWait();
    }

    private static Alert alert(Alert.AlertType type, String title, ErrorMessages message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message.header());
        alert.setContentText(message.text());
        return alert;
    }
}
