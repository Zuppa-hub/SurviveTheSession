package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.game.ExamReport;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Switches the screens of the window. Every FXML file has no {@code fx:controller}: the
 * navigator creates the controller with its dependencies and gives it to the loader, so the
 * controllers need no static state and no framework to receive what they use.
 */
public final class ViewNavigator {

    private static final String VIEWS = "/ui/";
    private static final String STYLESHEET = "/ui/styles.css";
    private static final double WIDTH = 960;
    private static final double HEIGHT = 640;

    private final Stage stage;
    private final GameService games;

    public ViewNavigator(Stage stage, GameService games) {
        this.stage = Objects.requireNonNull(stage, "Stage must not be null");
        this.games = Objects.requireNonNull(games, "Game service must not be null");
    }

    public void showMenu() {
        show("menu", new MenuController(this, games));
    }

    public void showNewGame() {
        show("new-game", new NewGameController(this, games));
    }

    /**
     * Shows a new or loaded game, after registering the listener that shows the outcome of each
     * exam. Called once per game, so the listener is registered once.
     */
    public void openGame(Game game) {
        game.addListener(report -> showOutcome(game, report));
        showSession(game);
    }

    public void showSession(Game game) {
        show("session", new SessionController(this, game));
    }

    /** Loads the exam screen and then starts the exam, whose first question arrives at once. */
    public void showExam(Game game, Exam exam) {
        ExamController controller = new ExamController(game, exam);
        show("exam", controller);
        controller.start();
    }

    public void showOutcome(Game game, ExamReport report) {
        show("outcome", new OutcomeController(this, game, report));
    }

    public void showGraduation(Game game) {
        show("graduation", new GraduationController(this, game));
    }

    private void show(String view, Object controller) {
        FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource(VIEWS + view + ".fxml"));
        loader.setController(controller);
        Parent root;
        try {
            root = loader.load();
        } catch (IOException error) {
            throw new UncheckedIOException("Cannot load the view " + view, error);
        }
        if (stage.getScene() == null) {
            Scene scene = new Scene(root, WIDTH, HEIGHT);
            scene.getStylesheets().add(ViewNavigator.class.getResource(STYLESHEET).toExternalForm());
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }
    }
}
