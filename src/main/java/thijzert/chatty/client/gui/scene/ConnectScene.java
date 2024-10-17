package thijzert.chatty.client.gui.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import thijzert.chatty.client.ReadThread;
import thijzert.chatty.data.Constants;
import thijzert.chatty.data.Data;
import thijzert.chatty.data.UserInfo;
import thijzert.chatty.data.WindowData;
import thijzert.chatty.functions.ProgramFunctions;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * In this scene you can connect to the server. It implements <code>ChattyScene</code>.
 *
 * @author Thijzert
 * @see ChattyScene
 */
public class ConnectScene implements ChattyScene {
    private final Scene scene_;
    private Socket socket_;
    private final TextField serverAddressTextField_;

    /**
     * Initializes the scene. It creates all the components, but it doesn't switch to the scene.
     */
    public ConnectScene(final WindowData windowData) {
        final Text welcomeText = new Text("Welcome to " + Constants.PROGRAM_NAME + "!");
        welcomeText.setFont(new Font(20));

        final Label serverAddressLabel = new Label("Address:");
        serverAddressLabel.setMinWidth(75);
        serverAddressTextField_ = TextFields.createClearableTextField();
        serverAddressTextField_.setPrefWidth(Constants.CONNECT_WINDOW_SIZE[0]);
        serverAddressTextField_.setPromptText("www.example.com");
        final Label serverPortLabel = new Label("Port:");
        serverPortLabel.setMinWidth(75);
        final TextField serverPortTextField = TextFields.createClearableTextField();
        serverPortTextField.setPrefWidth(Constants.CONNECT_WINDOW_SIZE[0]);
        serverPortTextField.setPromptText("5050");
        final Label nameLabel = new Label("Your name:");
        nameLabel.setMinWidth(75);
        final TextField nameTextField = TextFields.createClearableTextField();
        nameTextField.setPrefWidth(Constants.CONNECT_WINDOW_SIZE[0]);
        nameTextField.setPromptText("John Doe");

        final ValidationSupport validationSupport = new ValidationSupport();
        final Validator<String> intValidator = (control, value) -> {
            if (value.trim().isBlank()) {
                return ValidationResult.fromMessageIf(control, "Port number is required", Severity.ERROR, true);
            }
            try {
                Integer.parseInt(value.trim());
                return ValidationResult.fromMessageIf(control, "Not a number", Severity.ERROR, false);
            } catch (final NumberFormatException numberFormatException) {
                return ValidationResult.fromMessageIf(control, "Not a number", Severity.ERROR, true);
            }
        };
        final Validator<String> nameValidator = (control, value) -> {
            if (value.trim().isBlank()) {
                return ValidationResult.fromMessageIf(control, "Name is required", Severity.ERROR, true);
            } else if (value.trim().length() > Constants.MAX_NAME_LENGTH) {
                return ValidationResult.fromMessageIf(control, "Name is too long", Severity.ERROR, true);
            } else {
                return ValidationResult.fromMessageIf(control, "Name is too long", Severity.ERROR, false);
            }
        };
        validationSupport.registerValidator(serverAddressTextField_, false, Validator.createEmptyValidator("Address is required"));
        validationSupport.registerValidator(serverPortTextField, false, intValidator);
        validationSupport.registerValidator(nameTextField, false, nameValidator);

        final GridPane textFields = new GridPane();
        textFields.setAlignment(Pos.CENTER);
        textFields.setVgap(10);
        textFields.setHgap(15);
        textFields.add(serverAddressLabel, 0, 0);
        textFields.add(serverAddressTextField_, 1, 0);
        textFields.add(serverPortLabel, 0, 1);
        textFields.add(serverPortTextField, 1, 1);
        textFields.add(nameLabel, 0, 2);
        textFields.add(nameTextField, 1, 2);

        final Button connectButton = new Button("Connect to server");
        connectButton.setMaxWidth(Constants.CONNECT_WINDOW_SIZE[0] + 90);
        connectButton.setMinHeight(30);
        connectButton.setOnAction(event1 -> {
            connectButton.setDisable(true);
            if (!validationSupport.isInvalid()) {
                final String serverAddress = serverAddressTextField_.getText().trim().toLowerCase();
                final int serverPort = Integer.parseInt(serverPortTextField.getText().trim());
                final String name = nameTextField.getText().trim();

                final UserInfo userInfo = new UserInfo(name);
                Data.chatClientGui_.setUserInfo(userInfo);
                try {
                    socket_ = new Socket();
                    socket_.connect(new InetSocketAddress(serverAddress, serverPort), Constants.SOCKET_TIMEOUT_MS);

                    windowData.getStage().setOnCloseRequest(event2 -> {
                        try {
                            socket_.close();
                        } catch (final IOException ioException) {
                            System.exit(1);
                        }
                    });

                    new ReadThread(socket_, Data.chatClientGui_).start();
                    Data.objectOutputStream_ = new ObjectOutputStream(socket_.getOutputStream());
                    Data.objectOutputStream_.writeObject(userInfo);
                    next(windowData);
                } catch (final SocketTimeoutException socketTimeoutException) {
                    ProgramFunctions.showAlert(Alert.AlertType.ERROR, "It takes too long to connect to " + serverAddress + " at port " + serverPort + ".");
                } catch (final UnknownHostException unknownHostException) {
                    ProgramFunctions.showAlert(Alert.AlertType.ERROR, "Unable to connect to " + serverAddress + " at port " + serverPort + ".");
                } catch (final IOException ioException) {
                    ProgramFunctions.showThrowableAlert(ioException);
                }
            }
            connectButton.setDisable(false);
        });

        serverAddressTextField_.setOnAction(event -> serverPortTextField.requestFocus());
        serverPortTextField.setOnAction(event -> nameTextField.requestFocus());
        nameTextField.setOnAction(event -> connectButton.requestFocus());

        final VBox connectServerBox = new VBox(welcomeText, textFields, connectButton);
        connectServerBox.setPadding(new Insets(150));
        connectServerBox.setSpacing(20);
        connectServerBox.setAlignment(Pos.CENTER);

        scene_ = new Scene(connectServerBox, Constants.CONNECT_WINDOW_SIZE[0], Constants.CONNECT_WINDOW_SIZE[1]);
    }

    /**
     * Returns the connected socket.
     *
     * @return the connected socket
     */
    public Socket getSocket() {
        return socket_;
    }

    /**
     * This method switches to the home scene.
     *
     * @param windowData information of the current window
     */
    @Override
    public void start(final WindowData windowData) {
        Platform.runLater(() -> {
            windowData.getStage().setMinWidth(Constants.CONNECT_WINDOW_SIZE[0] * Constants.MIN_WINDOW_SIZE_FORMULA);
            windowData.getStage().setMinHeight(Constants.CONNECT_WINDOW_SIZE[1] * Constants.MIN_WINDOW_SIZE_FORMULA);
            windowData.getStage().setWidth(Constants.CONNECT_WINDOW_SIZE[0]);
            windowData.getStage().setHeight(Constants.CONNECT_WINDOW_SIZE[1]);
            windowData.getStage().setScene(scene_);
            serverAddressTextField_.requestFocus();
        });
    }

    /**
     * This method is implemented of <code>ChattyScene</code>. It changes the min. window size back to the default settings.
     *
     * @param windowData information of the current window
     */
    @Override
    public void next(final WindowData windowData) {
        windowData.getStage().setMinWidth(Constants.DEFAULT_WINDOW_SIZE[0] * Constants.MIN_WINDOW_SIZE_FORMULA);
        windowData.getStage().setMinHeight(Constants.DEFAULT_WINDOW_SIZE[1] * Constants.MIN_WINDOW_SIZE_FORMULA);
        windowData.getStage().setWidth(Constants.DEFAULT_WINDOW_SIZE[0]);
        windowData.getStage().setHeight(Constants.DEFAULT_WINDOW_SIZE[1]);
        ProgramFunctions.startScene(Data.mainScene_, windowData);
    }
}
