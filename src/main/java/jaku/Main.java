package jaku;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** JavaFX entry point for Jaku. */
public class Main extends Application {
    /** Creates and displays Jaku's chat window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<jaku.ui.MainWindow>getController().setJaku(new Jaku());
            stage.setScene(new Scene(root));
            stage.setTitle("Jaku");
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Jaku's main window.", exception);
        }
    }
}
