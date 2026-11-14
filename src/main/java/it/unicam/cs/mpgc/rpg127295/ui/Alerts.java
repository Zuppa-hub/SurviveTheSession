package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import javafx.scene.control.Alert;

/** The dialogs of the game. To be called on the JavaFX Application Thread. */
public final class Alerts {

    private Alerts() {
    }

    /**
     * Warns that an automatic save failed, without blocking the game: the dialog does not wait
     * for the player.
     */
    public static void saveFailed(SaveGameException failure) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Salvataggio non riuscito");
        alert.setHeaderText("La partita non è stata salvata, ma puoi continuare a giocare.");
        alert.setContentText(failure.getMessage());
        alert.show();
    }

    public static void error(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
