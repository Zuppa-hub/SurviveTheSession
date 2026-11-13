package it.unicam.cs.mpgc.rpg127295.ui;

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

    public void showSession(Game game) {
        show("session", new SessionController(this, game));
    }

    /** The exam screen arrives with issue #11. */
    public void showExam(Game game, Exam exam) {
        Alerts.info("Esame " + exam.subject().name(), "La schermata d'esame arriva con la issue #11.");
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
