package jaku.ui;

import jaku.MessageKind;
import javafx.beans.value.ObservableNumberValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** A styled chat message displayed in Jaku's graphical interface. */
public class DialogBox extends HBox {
    /** Text node that is width-constrained as the conversation pane changes size. */
    private final Label label;

    private DialogBox(String text, boolean user, MessageKind messageKind) {
        label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("message-bubble");
        label.getStyleClass().add(getMessageStyleClass(user, messageKind));
        setAlignment(user ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        getChildren().add(label);
    }

    /** Creates a dialog containing a user command. */
    public static DialogBox user(String text) {
        return new DialogBox(text, true, MessageKind.REPLY);
    }

    /**
     * Creates a dialog containing Jaku's response with the appropriate visual emphasis.
     *
     * @param text response text
     * @param messageKind response category
     * @return styled Jaku dialog
     */
    public static DialogBox reply(String text, MessageKind messageKind) {
        return new DialogBox(text, false, messageKind);
    }

    /**
     * Binds this dialog's bubble to a responsive maximum width.
     *
     * @param maximumWidth maximum bubble width within the conversation pane
     */
    public void bindMaximumBubbleWidth(ObservableNumberValue maximumWidth) {
        label.maxWidthProperty().bind(maximumWidth);
    }

    /** Chooses the message style from its speaker and category. */
    private String getMessageStyleClass(boolean user, MessageKind messageKind) {
        if (user) {
            return "user-message";
        }
        return messageKind == MessageKind.ERROR ? "error-message" : "jaku-message";
    }
}
