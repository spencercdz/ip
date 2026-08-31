package jaku.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** A styled chat message displayed in Jaku's graphical interface. */
public class DialogBox extends HBox {
    private DialogBox(String text, boolean user) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add(user ? "user-message" : "jaku-message");
        setAlignment(user ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        getChildren().add(label);
    }

    /** Creates a dialog containing a user command. */
    public static DialogBox user(String text) {
        return new DialogBox(text, true);
    }

    /** Creates a dialog containing Jaku's response. */
    public static DialogBox reply(String text) {
        return new DialogBox(text, false);
    }
}
