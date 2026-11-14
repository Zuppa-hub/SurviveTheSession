package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.game.ExamReport;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** The outcome of an exam: grade or failure, reward, levels; then the session or the graduation. */
public final class OutcomeController {

    private final ViewNavigator navigator;
    private final Game game;
    private final ExamReport report;

    @FXML
    private Label examLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label gradeLabel;
    @FXML
    private VBox detailsBox;
    @FXML
    private Button continueButton;

    OutcomeController(ViewNavigator navigator, Game game, ExamReport report) {
        this.navigator = Objects.requireNonNull(navigator, "Navigator must not be null");
        this.game = Objects.requireNonNull(game, "Game must not be null");
        this.report = Objects.requireNonNull(report, "Report must not be null");
    }

    @FXML
    private void initialize() {
        OutcomeSummary summary = OutcomeSummary.of(report);
        examLabel.setText(report.exam().subject().name());
        titleLabel.setText(summary.title());
        gradeLabel.setText(summary.grade().map(grade -> "Voto " + grade).orElse("Nessun voto"));
        gradeLabel.getStyleClass().add(summary.grade().isPresent() ? "success" : "error");
        detailsBox.getChildren().setAll(summary.details().stream().map(Label::new).toList());
        continueButton.setText(game.canGraduate() ? "Vai alla laurea" : "Torna alla sessione");
    }

    @FXML
    private void onContinue() {
        if (game.canGraduate()) {
            navigator.showGraduation(game);
        } else {
            navigator.showSession(game);
        }
    }
}
