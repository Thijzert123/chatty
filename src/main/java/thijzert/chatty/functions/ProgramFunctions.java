package thijzert.chatty.functions;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import thijzert.chatty.client.gui.scene.ChattyScene;
import thijzert.chatty.data.Constants;
import thijzert.chatty.data.Data;
import thijzert.chatty.data.WindowData;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This is a class of some important (basic) functions for the program.
 *
 * @author Thijzert
 */
public class ProgramFunctions {
    /**
     * This method switches the scene to another.
     *
     * @param scene      the scene this function needs to switch to
     * @param windowData the data the function needs to give to the new scene
     */
    public static void startScene(final ChattyScene scene, final WindowData windowData) {
        scene.start(windowData);
    }

    /**
     * Shows a default yet custom alert for Chatty.
     *
     * @param alertType the type for the alert
     * @param text      the text for the context of the alert
     * @see Alert
     * @see AlertType
     */
    public static void showAlert(final AlertType alertType, final String text) {
        Platform.runLater(() -> {
            final Alert alert = new Alert(alertType);
            alert.setTitle(Constants.PROGRAM_NAME);
            alert.setHeaderText(null);
            alert.setContentText(text);
            alert.initOwner(Data.windowData_.getStage());
            alert.showAndWait();
        });
    }

    /**
     * Shows an error alert and the stacktrace of a <code>Throwable</code>.
     *
     * @param throwable the throwable
     * @see Alert
     * @see AlertType#ERROR
     * @see Throwable
     * @see Throwable#printStackTrace()
     */
    public static void showThrowableAlert(final Throwable throwable) {
        Platform.runLater(() -> {
            final Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(Constants.PROGRAM_NAME);
            alert.setHeaderText("Oops, something went wrong!");
            alert.setContentText(throwable.getMessage());
            alert.initOwner(Data.windowData_.getStage());

            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);

            final Label stacktraceLabel = new Label("The throwable stacktrace was:");

            final TextArea throwableArea = new TextArea(stringWriter.toString());
            throwableArea.setEditable(false);
            throwableArea.setWrapText(true);
            throwableArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            GridPane.setVgrow(throwableArea, Priority.ALWAYS);
            GridPane.setHgrow(throwableArea, Priority.ALWAYS);

            final GridPane expandableContent = new GridPane();
            expandableContent.setMaxWidth(Double.MAX_VALUE);
            expandableContent.setVgap(5);
            expandableContent.add(stacktraceLabel, 0, 0);
            expandableContent.add(throwableArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expandableContent);
            alert.showAndWait();
        });
    }
}
