package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/** Main menu: new game, load a saved game, exit. */
public final class MenuController {

    private final ViewNavigator navigator;
    private final GameService games;

    @FXML
    private ListView<String> savedGames;
    @FXML
    private Button loadButton;

    MenuController(ViewNavigator navigator, GameService games) {
        this.navigator = Objects.requireNonNull(navigator, "Navigator must not be null");
        this.games = Objects.requireNonNull(games, "Game service must not be null");
    }

    @FXML
    private void initialize() {
        savedGames.getItems().setAll(games.savedGames());
        loadButton.disableProperty().bind(savedGames.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onNewGame() {
        navigator.showNewGame();
    }

    @FXML
    private void onLoad() {
        String slot = savedGames.getSelectionModel().getSelectedItem();
        try {
            games.loadGame(slot).ifPresentOrElse(navigator::showSession,
                    () -> Alerts.error("Partita non trovata", "Il salvataggio " + slot + " non esiste più."));
        } catch (SaveGameException error) {
            Alerts.error("Impossibile caricare la partita", error.getMessage());
        }
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }
}
