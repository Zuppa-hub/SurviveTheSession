package it.unicam.cs.mpgc.rpg127295.ui;

import java.util.List;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * "Come si gioca": the pages of {@link HowToPlay}, one at a time with Avanti and Indietro. When
 * the player closes them the screen runs the action it was given, so the same pages can lead
 * to the new game, back to the menu or back to the session.
 */
public final class HowToPlayController {

    private final List<HowToPlay.Page> pages;
    private final Runnable onClose;
    private final String closeLabel;
    private int current;

    @FXML
    private Label progressLabel;
    @FXML
    private Label pageTitleLabel;
    @FXML
    private Label pageTextLabel;
    @FXML
    private Button skipButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;

    /**
     * @param onClose    what to show when the player closes the pages
     * @param closeLabel the text of the button that closes them, such as "Inizia"
     */
    HowToPlayController(GameGuide guide, Runnable onClose, String closeLabel) {
        this.pages = HowToPlay.pages(Objects.requireNonNull(guide, "Guide must not be null"));
        this.onClose = Objects.requireNonNull(onClose, "Close action must not be null");
        this.closeLabel = Objects.requireNonNull(closeLabel, "Close label must not be null");
    }

    @FXML
    private void initialize() {
        skipButton.setText("Salta");
        showPage();
    }

    @FXML
    private void onPrevious() {
        current--;
        showPage();
    }

    @FXML
    private void onNext() {
        if (isLastPage()) {
            onClose();
            return;
        }
        current++;
        showPage();
    }

    @FXML
    private void onClose() {
        onClose.run();
    }

    private boolean isLastPage() {
        return current == pages.size() - 1;
    }

    private void showPage() {
        HowToPlay.Page page = pages.get(current);
        progressLabel.setText("Pagina " + (current + 1) + " di " + pages.size());
        pageTitleLabel.setText(page.title());
        pageTextLabel.setText(page.text());
        previousButton.setDisable(current == 0);
        nextButton.setText(isLastPage() ? closeLabel : "Avanti");
        skipButton.setVisible(!isLastPage());
    }
}
