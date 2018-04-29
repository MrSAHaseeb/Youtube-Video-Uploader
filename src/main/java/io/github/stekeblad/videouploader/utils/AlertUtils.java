package io.github.stekeblad.videouploader.utils;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * A set of prepared Alert dialogs that can easy be created and used.
 * Several properties is already defined you just need to provide the text to be displayed.
 */
public class AlertUtils {

    /**
     * Convenient method for creating a @code{Alert} dialog with a single CLOSE button
     * @param header Custom window title
     * @param content The message to display to the user
     * @return a Alert ready to display or continue to modify
     */
    public static Alert simpleClose(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(header);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(ButtonType.CLOSE);
        return alert;
    }

    /**
     * Convenient method for creating a @code{Alert} dialog with a YES and a NO button.
     * To set what to do on the buttons you can do like this:
     * <pre>{@code
     * Optional<ButtonType> buttonChoice = AlertUtils.yesNo(...).showAndWait();
     * if (buttonChoice.isPresent()) {
     *     if (buttonChoice.get() == ButtonType.YES) {
     *         doOnYes();
     *     } else { // ButtonType.NO or Closed with [X] button
     *         doOnNo();
     *     }
     * }
     *
     * @param header Custom window title
     * @param content The message to display to the user
     * @return a Alert that needs actions bound to the buttons but is otherwise ready to be shown
     */
    public static Alert yesNo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(header);
        alert.setContentText(content);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert;
    }

    /**
     * A Dialog with three button where you define not only the header and content text but also the text on the buttons
     * @param header Custom window title
     * @param content The message to display to the user
     * @param btn1Text The text on the first button
     * @param btn2Text The text on the second button
     * @param btn3Text The text on the third button
     * @return the text on the button that was clicked or null if no button result is available
     */
    public static String threeButtons(String header, String content, String btn1Text, String btn2Text, String btn3Text) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(header);
        alert.setContentText(content);
        alert.getButtonTypes().addAll(new ButtonType(btn1Text), new ButtonType(btn2Text), new ButtonType(btn3Text));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            return result.get().getText();
        } else {
            return null;
        }
    }

    /**
     * Creates a GridPane with a TextArea at the top and a Button in the bottom right corner. Clicking the button will
     * cause the window this pane is in to be closed. longMsg defines the text to be in the TextArea and wrapText defines
     * if the text in the TextArea should get automatic line wrapping or not
     *
     * @param longMsg  The text to appear in the TextArea
     * @param wrapText true if the content of the TextArea should wrap automatically, false if it should not wrap.
     * @return the GridPane
     */
    private static GridPane makeLongMsgPane(String longMsg, boolean wrapText) {
        TextArea textArea = new TextArea(longMsg);
        textArea.setEditable(false);
        textArea.setWrapText(wrapText);
        Button closeButton = new Button("Close");
        closeButton.setOnMouseClicked(event -> closeButton.getScene().getWindow().hide());

        GridPane pane = new GridPane();
        GridPane.setMargin(textArea, new Insets(3, 0, 0, 0));
        GridPane.setMargin(closeButton, new Insets(3, 0, 0, 0));
        pane.add(textArea, 0, 0);
        pane.add(closeButton, 0, 1);
        GridPane.setHalignment(closeButton, HPos.RIGHT);
        return pane;
    }

    /**
     * Takes a Pane and creates a window of it, the window will have Modality.WINDOW_MODAL, always be on top will not be resizeable
     *
     * @param pane   a pane to put in a new window
     * @param header the title of the new window
     */
    private static void paneToWindow(Pane pane, String header) {
        Scene alertDialog = new Scene(pane);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(alertDialog);
        stage.setTitle(header);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Like simpleClose but designed for longer messages in the content area, it does also displays the window directly
     *
     * @param header  Custom window title
     * @param content The message to display to the user
     */
    public static void simpleClose_longContent(String header, String content) {
        GridPane pane = makeLongMsgPane(content, true);
        paneToWindow(pane, header);
    }

    /**
     * Shows a window with details about an Exception. The displayed message will look something like this:
     * The parameter content, exception.getMessage(), the stacktrace.
     *
     * @param header    Custom window title
     * @param content   information about the error to show the user that can not be extracted from the exception
     * @param exception the exception that occurred
     */
    public static void exceptionDialog(String header, String content, Throwable exception) {
        String stackTrace = getStacktrace(exception);
        String fullContent = content +
                "\nHere is the error details:" +
                "\n-----------------------------------------" +
                "\n" + exception.getMessage() +
                "\n-----------------------------------------" +
                "\n" + stackTrace;
        GridPane pane = makeLongMsgPane(fullContent, false);
        paneToWindow(pane, header);
    }

    /**
     * Returns as a String what exception.printStackTrace would have printed to the console.
     *
     * @param exception the Exception you want the stacktrace of.
     * @return the stacktrace as a String.
     */
    public static String getStacktrace(Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }
}
