package jaku;

import javafx.application.Application;

/** Starts Jaku's JavaFX application. */
public final class Launcher {
    private Launcher() {
    }

    /** Launches the graphical interface. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
