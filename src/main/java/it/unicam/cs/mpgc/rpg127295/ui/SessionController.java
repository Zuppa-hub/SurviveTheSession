package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.transcript.Transcript;
import java.util.Locale;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

/** The session: exams, stats, inventory and transcript of the student, and the exam to take. */
public final class SessionController {

    private final ViewNavigator navigator;
    private final Game game;

    @FXML
    private Label studentLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label knowledgeLabel;
    @FXML
    private ProgressBar energyBar;
    @FXML
    private Label energyLabel;
    @FXML
    private ProgressBar concentrationBar;
    @FXML
    private Label concentrationLabel;
    @FXML
    private ListView<ExamRow> exams;
    @FXML
    private ListView<String> inventory;
    @FXML
    private Label averageLabel;
    @FXML
    private Label creditsLabel;
    @FXML
    private Label graduationLabel;
    @FXML
    private Button examButton;

    SessionController(ViewNavigator navigator, Game game) {
        this.navigator = Objects.requireNonNull(navigator, "Navigator must not be null");
        this.game = Objects.requireNonNull(game, "Game must not be null");
    }

    @FXML
    private void initialize() {
        showStudent(game.student());
        showTranscript(game.student().transcript());
        exams.setCellFactory(list -> new ExamCell());
        exams.getItems().setAll(ExamRow.of(game));
        examButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !isAvailable(exams.getSelectionModel().getSelectedItem()),
                exams.getSelectionModel().selectedItemProperty()));
    }

    @FXML
    private void onTakeExam() {
        navigator.showExam(game, exams.getSelectionModel().getSelectedItem().exam());
    }

    @FXML
    private void onMenu() {
        navigator.showMenu();
    }

    private static boolean isAvailable(ExamRow row) {
        return row != null && row.status() == ExamRow.Status.AVAILABLE;
    }

    private void showStudent(Student student) {
        Stats stats = student.stats();
        studentLabel.setText(student.name() + " · " + student.profile().name());
        levelLabel.setText("Livello " + student.level().number() + " · " + student.level().experience() + " XP");
        knowledgeLabel.setText("Conoscenza " + stats.knowledge());
        energyBar.setProgress((double) stats.energy() / stats.maxEnergy());
        energyLabel.setText("Energia " + stats.energy() + " / " + stats.maxEnergy());
        concentrationBar.setProgress((double) stats.concentration() / stats.maxConcentration());
        concentrationLabel.setText("Concentrazione " + stats.concentration() + " / " + stats.maxConcentration());
        inventory.getItems().setAll(student.inventory().items().entrySet().stream()
                .map(entry -> entry.getKey().name() + " × " + entry.getValue())
                .sorted()
                .toList());
    }

    private void showTranscript(Transcript transcript) {
        averageLabel.setText(transcript.weightedAverage()
                .stream()
                .mapToObj(average -> String.format(Locale.ITALY, "Media ponderata %.2f", average))
                .findFirst()
                .orElse("Nessun esame superato"));
        creditsLabel.setText(transcript.totalCredits() + " CFU");
        graduationLabel.setText(game.graduationGrade()
                .map(grade -> "Sessione completata: voto di laurea " + grade + "/110")
                .orElse(""));
    }

    /** An exam with its type, credits and status; the status sets the style. */
    private static final class ExamCell extends ListCell<ExamRow> {

        @Override
        protected void updateItem(ExamRow row, boolean empty) {
            super.updateItem(row, empty);
            getStyleClass().removeAll("exam-passed", "exam-available", "exam-locked");
            if (empty || row == null) {
                setText(null);
                return;
            }
            setText(row.exam().subject().name() + "\n" + row.typeLabel() + " · " + row.exam().credits()
                    + " CFU · " + row.statusLabel());
            getStyleClass().add("exam-" + row.status().name().toLowerCase(Locale.ROOT));
        }
    }
}
