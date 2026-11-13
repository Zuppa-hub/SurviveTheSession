package it.unicam.cs.mpgc.rpg127295.app;

import javafx.application.Application;

/**
 * Entry point of the application: it launches {@link GameApplication}. A separate class that
 * does not extend {@code Application} lets the game start from the class path.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Application.launch(GameApplication.class, args);
    }
}
