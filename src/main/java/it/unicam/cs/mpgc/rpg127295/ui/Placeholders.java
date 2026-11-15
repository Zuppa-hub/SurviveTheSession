package it.unicam.cs.mpgc.rpg127295.ui;

import javafx.scene.control.Label;

/** What an empty list shows: the text of {@link EmptyStates}, wrapped. */
final class Placeholders {

    private Placeholders() {
    }

    static Label of(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }
}
