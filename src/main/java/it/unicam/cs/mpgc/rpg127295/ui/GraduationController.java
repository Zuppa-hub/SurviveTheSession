package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/** The graduation: transcript, weighted average, credits and final grade. */
public final class GraduationController {

    private final ViewNavigator navigator;
    private final Game game;

    @FXML
    private Label congratulationsLabel;
    @FXML
    private Label finalGradeLabel;
    @FXML
    private Label averageLabel;
    @FXML
    private Label creditsLabel;
    @FXML
    private ListView<String> exams;

    GraduationController(ViewNavigator navigator, Game game) {
        this.navigator = Objects.requireNonNull(navigator, "Navigator must not be null");
        this.game = Objects.requireNonNull(game, "Game must not be null");
    }

    @FXML
    private void initialize() {
        GraduationSummary summary = GraduationSummary.of(game);
        congratulationsLabel.setText("Congratulazioni, " + summary.studentName() + "!");
        finalGradeLabel.setText("Voto di laurea " + summary.finalGrade() + "/110");
        averageLabel.setText("Media ponderata " + summary.averageLabel());
        creditsLabel.setText(summary.credits() + " CFU");
        exams.getItems().setAll(summary.exams());
    }

    @FXML
    private void onMenu() {
        navigator.showMenu();
    }
}
