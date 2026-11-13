package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/** New game: the player's name and profile. The save slot is derived from the name. */
public final class NewGameController {

    private final ViewNavigator navigator;
    private final GameService games;

    @FXML
    private TextField nameField;
    @FXML
    private ListView<StudentProfile> profiles;
    @FXML
    private Label errorLabel;

    NewGameController(ViewNavigator navigator, GameService games) {
        this.navigator = Objects.requireNonNull(navigator, "Navigator must not be null");
        this.games = Objects.requireNonNull(games, "Game service must not be null");
    }

    @FXML
    private void initialize() {
        profiles.getItems().setAll(games.profiles());
        profiles.setCellFactory(list -> new ProfileCell());
        profiles.getSelectionModel().selectFirst();
    }

    @FXML
    private void onStart() {
        String name = nameField.getText().strip();
        StudentProfile profile = profiles.getSelectionModel().getSelectedItem();
        if (name.isEmpty() || profile == null) {
            errorLabel.setText("Scrivi il tuo nome e scegli un profilo.");
            return;
        }
        String slot = SlotNames.fromPlayerName(name);
        if (games.savedGames().contains(slot)) {
            errorLabel.setText("Esiste già una partita salvata come \"" + slot + "\": scegli un altro nome.");
            return;
        }
        navigator.showSession(games.newGame(name, profile.id(), slot));
    }

    @FXML
    private void onBack() {
        navigator.showMenu();
    }

    /** A profile with its description and starting stats. */
    private static final class ProfileCell extends ListCell<StudentProfile> {

        @Override
        protected void updateItem(StudentProfile profile, boolean empty) {
            super.updateItem(profile, empty);
            if (empty || profile == null) {
                setText(null);
                return;
            }
            Stats stats = profile.initialStats();
            setText(profile.name() + "\n" + profile.description() + "\nConoscenza " + stats.knowledge()
                    + " · Energia " + stats.maxEnergy() + " · Concentrazione " + stats.maxConcentration());
        }
    }
}
