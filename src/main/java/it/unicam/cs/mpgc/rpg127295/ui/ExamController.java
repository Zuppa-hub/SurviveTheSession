package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleListener;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattle;
import it.unicam.cs.mpgc.rpg127295.engine.battle.PresentedQuestion;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The exam: question, answer, timer, bars, items and abilities. It listens to the battle and
 * shows what changed; every decision is the battle's. The battle in progress is read from the
 * game ({@link Game#currentBattle()}), which has it before the first question is presented.
 * <p>
 * When the battle ends the game reports it to its listeners, and the navigator shows the
 * outcome: this screen does nothing at the end.
 */
public final class ExamController implements BattleListener {

    private static final int FEW_SECONDS = 5;

    private final Game game;
    private final Exam exam;
    private final GameGuide guide;
    private boolean answering;

    @FXML
    private Label examLabel;
    @FXML
    private Label examDetailsLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private ProgressBar resistanceBar;
    @FXML
    private Label resistanceLabel;
    @FXML
    private ProgressBar energyBar;
    @FXML
    private Label energyLabel;
    @FXML
    private ProgressBar concentrationBar;
    @FXML
    private Label concentrationLabel;
    @FXML
    private Label difficultyLabel;
    @FXML
    private Label promptLabel;
    @FXML
    private Label codeLabel;
    @FXML
    private Label hintLabel;
    @FXML
    private VBox optionsBox;
    @FXML
    private HBox answerBox;
    @FXML
    private TextField answerField;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Label boostLabel;
    @FXML
    private FlowPane actionsBox;

    ExamController(Game game, Exam exam, GameGuide guide) {
        this.game = Objects.requireNonNull(game, "Game must not be null");
        this.exam = Objects.requireNonNull(exam, "Exam must not be null");
        this.guide = Objects.requireNonNull(guide, "Guide must not be null");
    }

    @FXML
    private void initialize() {
        examLabel.setText(exam.subject().name());
        examDetailsLabel.setText(Labels.of(exam.type()) + " · " + StatTexts.credits(exam.credits()));
        boostLabel.setText("Risposta potenziata: la prossima risposta giusta toglie ×"
                + guide.battleRules().boostedDamageMultiplier() + " di resistenza");
    }

    /** Starts the exam: called once the view is loaded, because the first question arrives at once. */
    void start() {
        game.startExam(exam, this);
    }

    @Override
    public void onQuestionPresented(PresentedQuestion question, OptionalInt secondsToAnswer) {
        answerField.clear();
        show(timerLabel, secondsToAnswer.isPresent());
        secondsToAnswer.ifPresent(this::showSecondsLeft);
        refresh();
    }

    @Override
    public void onAnswerEvaluated(boolean correct, BattleState state) {
        AnswerFeedback feedback = AnswerFeedback.of(correct, answering);
        Arrays.stream(AnswerFeedback.values()).forEach(each -> feedbackLabel.getStyleClass().remove(each.styleClass()));
        feedbackLabel.getStyleClass().add(feedback.styleClass());
        feedbackLabel.setText(feedback.message());
    }

    @Override
    public void onTimeTick(int secondsLeft) {
        showSecondsLeft(secondsLeft);
    }

    @Override
    public void onEffectApplied(BattleState state) {
        refresh();
    }

    @FXML
    private void onSubmit() {
        String text = answerField.getText().strip();
        if (!text.isEmpty()) {
            answer(text);
        }
    }

    @FXML
    private void onRetire() {
        inBattle(ExamBattle::retire);
    }

    private void answer(String text) {
        answering = true;
        try {
            inBattle(battle -> battle.answer(text));
        } finally {
            answering = false;
        }
    }

    private void inBattle(Consumer<ExamBattle> action) {
        game.currentBattle().ifPresent(action);
    }

    private void refresh() {
        inBattle(battle -> {
            BattleState state = battle.state();
            showBars(state);
            state.currentQuestion().ifPresent(this::showQuestion);
            show(boostLabel, state.nextCorrectAnswerDoubled());
            showActions(battle);
        });
    }

    private void showBars(BattleState state) {
        Stats stats = state.stats();
        resistanceBar.setProgress(StatTexts.share(state.resistance(), state.maxResistance()));
        resistanceLabel.setText(StatTexts.resistance(state.resistance(), state.maxResistance()));
        energyBar.setProgress(StatTexts.share(stats.energy(), stats.maxEnergy()));
        energyLabel.setText(StatTexts.energy(stats));
        concentrationBar.setProgress(StatTexts.share(stats.concentration(), stats.maxConcentration()));
        concentrationLabel.setText(StatTexts.concentration(stats));
    }

    private void showQuestion(PresentedQuestion presented) {
        difficultyLabel.setText(Labels.of(presented.question().difficulty()));
        promptLabel.setText(presented.question().prompt());
        codeLabel.setText(presented.question().codeSnippet().orElse(""));
        show(codeLabel, presented.question().codeSnippet().isPresent());
        hintLabel.setText(presented.hintVisible() ? "Suggerimento: " + presented.question().hint().orElseThrow() : "");
        show(hintLabel, presented.hintVisible());
        boolean fillIn = presented.options().isEmpty();
        show(answerBox, fillIn);
        show(optionsBox, !fillIn);
        optionsBox.getChildren().setAll(presented.options().stream().map(this::optionButton).toList());
        if (fillIn) {
            answerField.requestFocus();
        }
    }

    private Button optionButton(String option) {
        Button button = new Button(option);
        button.getStyleClass().add("option");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setWrapText(true);
        button.setOnAction(event -> answer(option));
        return button;
    }

    private void showActions(ExamBattle battle) {
        actionsBox.getChildren().setAll(BattleAction.of(battle, game.student().inventory()).stream()
                .map(this::actionButton)
                .toList());
    }

    private Button actionButton(BattleAction action) {
        Button button = new Button(action.label());
        button.getStyleClass().add("secondary");
        button.setTooltip(new Tooltip(action.description()));
        button.setDisable(!action.enabled());
        button.setOnAction(event -> action.use().run());
        return button;
    }

    private void showSecondsLeft(int seconds) {
        timerLabel.setText(seconds + " s");
        timerLabel.getStyleClass().remove("timer-low");
        if (seconds <= FEW_SECONDS) {
            timerLabel.getStyleClass().add("timer-low");
        }
    }

    private static void show(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
