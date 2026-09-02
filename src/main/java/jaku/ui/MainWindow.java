package jaku.ui;

import jaku.Jaku;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Controller for Jaku's main chat window. */
public class MainWindow {
    /** Console divider emitted by the shared command service. */
    private static final String RESPONSE_DIVIDER = "(?m)^_{60}\\R?";

    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    private Jaku jaku;

    /** Injects Jaku's UI-independent command service. */
    public void setJaku(Jaku jaku) {
        this.jaku = jaku;
        dialogContainer.getChildren().add(DialogBox.reply("Welcome back! What would you like to plan today?"));
    }

    /** Scrolls new messages into view. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Sends the text-field command and shows Jaku's response. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.user(input));
        dialogContainer.getChildren().add(DialogBox.reply(formatForGui(jaku.getResponse(input))));
        userInput.clear();
        if (jaku.isExitRequested()) {
            Platform.runLater(Platform::exit);
        }
    }

    /**
     * Removes console-only framing before displaying a response in the GUI.
     *
     * @param response response emitted by Jaku's shared command service
     * @return response suitable for a chat bubble
     */
    private String formatForGui(String response) {
        return response.replaceAll(RESPONSE_DIVIDER, "").strip();
    }
}
