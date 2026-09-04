package bo.gui;

import javafx.application.Application;

/** Launches the JavaFX application without triggering the JavaFX classpath issue. */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to the JavaFX application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
