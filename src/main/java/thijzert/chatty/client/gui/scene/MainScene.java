package thijzert.chatty.client.gui.scene;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.WordUtils;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import thijzert.chatty.client.ChatClient;
import thijzert.chatty.data.*;
import thijzert.chatty.functions.ProgramFunctions;
import thijzert.chatty.message.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

/**
 * The main scene of Chatty GUI (home scene). It implements <code>ThijzertScene</code>.
 *
 * @author Thijzert
 * @see ChattyScene
 */
public final class MainScene implements ChattyScene {
    private final Scene scene_;
    private final WindowData windowData_;
    private final ListView<Message> receiveMessagesListView_;
    private final Label sendMessageLabel_;
    private final TextField sendMessageTextField_;
    private final Glyph attachGlyph_;
    private final Button attachButton_;
    private final ListView<UserInfo> connectedUsersListView_;

    private static final Border messageBorder_ = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(7.5), new BorderWidths(1.5)));
    private static final ObservableList<UserInfo> blockedUsers_ = FXCollections.observableArrayList();

    private File attachedFile_;

    /**
     * Initializes the class. It creates all the components, but it doesn't switch to the scene.
     */
    public MainScene(final WindowData windowData) {
        windowData_ = windowData;
        attachedFile_ = null;

        final MenuItem disconnectMenuItem = new MenuItem("_Disconnect");
        disconnectMenuItem.setOnAction(event -> {
            try {
                Data.connectScene_.getSocket().close();
                ProgramFunctions.startScene(Data.connectScene_, windowData);
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
        });

        final MenuItem exitMenuItem = new MenuItem("E_xit");
        exitMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));
        exitMenuItem.setOnAction(event -> {
            try {
                Data.connectScene_.getSocket().close();
                Platform.exit();
            } catch (final IOException ioException) {
                Platform.exit();
            }
        });

        final Menu fileMenu = new Menu("_File", null, disconnectMenuItem, exitMenuItem);
        final MenuBar menuBar = new MenuBar(fileMenu);
        GridPane.setHgrow(menuBar, Priority.ALWAYS);

        receiveMessagesListView_ = new ListView<>();
        receiveMessagesListView_.setEditable(false);
        receiveMessagesListView_.setFocusTraversable(false);
        receiveMessagesListView_.setCellFactory(param -> new MessageListCell(windowData));
        VBox.setVgrow(receiveMessagesListView_, Priority.ALWAYS);

        sendMessageLabel_ = new Label();
        sendMessageLabel_.setFont(Font.font(Constants.DEFAULT_FONT_FAMILY, FontWeight.BOLD, 14));
        sendMessageLabel_.setPadding(new Insets(5));
        sendMessageTextField_ = new TextField();
        HBox.setHgrow(sendMessageTextField_, Priority.ALWAYS);
        sendMessageTextField_.setOnAction(event -> {
            sendMessage(sendMessageTextField_.getText());
            sendMessageTextField_.clear();
        });
        final Button sendMessageButton = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.SEND));
        sendMessageButton.setOnAction(event -> {
            sendMessage(sendMessageTextField_.getText());
            sendMessageTextField_.clear();
        });

        final Label attachedFileLabel = new Label();
        attachedFileLabel.setWrapText(true);
        attachedFileLabel.setMaxWidth(250);
        attachedFileLabel.setPadding(new Insets(0, 0, 5, 0));
        attachedFileLabel.setAlignment(Pos.CENTER);
        final Button removeAttachedFileButton = new Button("Remove attachment");
        removeAttachedFileButton.setMaxWidth(Double.MAX_VALUE);
        final Button replaceAttachedFileButton = new Button("Replace attachment");
        replaceAttachedFileButton.setMaxWidth(Double.MAX_VALUE);
        final VBox attachedFileBox = new VBox(attachedFileLabel, removeAttachedFileButton, replaceAttachedFileButton); // TODO style this
        attachedFileBox.setPadding(new Insets(10));
        attachedFileBox.setSpacing(5);
        final PopOver attachedFilePopOver = new PopOver(attachedFileBox);
        attachedFilePopOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        attachGlyph_ = new Glyph("FontAwesome", FontAwesome.Glyph.PAPERCLIP);
        attachGlyph_.setRotate(-20);
        attachGlyph_.setScaleY(-1);
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to attach");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*"),
                new FileChooser.ExtensionFilter("Image Files", "*.bmp", ".gif", ".jpeg", ".jpg", ".png"));
        attachButton_ = new Button("", attachGlyph_);
        attachButton_.setTooltip(new Tooltip("Attach a file"));
        attachButton_.setOnAction(event -> {
            if (attachedFile_ == null) {
                attachedFile_ = fileChooser.showOpenDialog(windowData.getStage());
                if (attachedFile_ != null) {
                    final Glyph fileGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.FILE);
                    fileGlyph.setStyle("-fx-text-fill:black;");
                    attachButton_.setGraphic(fileGlyph);
                    attachButton_.setTooltip(new Tooltip("A file is attached"));
                    attachedFileLabel.setText(attachedFile_.getName());
                }
            } else {
                attachedFilePopOver.show(attachButton_);
            }
        });
        removeAttachedFileButton.setOnAction(event -> {
            attachedFile_ = null;
            attachButton_.setTooltip(new Tooltip("Attach a file"));
            attachButton_.setGraphic(attachGlyph_);
            attachedFilePopOver.hide();
        });
        replaceAttachedFileButton.setOnAction(event -> {
            attachedFile_ = fileChooser.showOpenDialog(windowData.getStage());
            if (attachedFile_ == null) {
                attachButton_.setTooltip(new Tooltip("Attach a file"));
                attachButton_.setGraphic(attachGlyph_);
            }
            attachedFilePopOver.hide();
        });
        final HBox sendMessageBox = new HBox(sendMessageLabel_, sendMessageTextField_, attachButton_, sendMessageButton);
        sendMessageBox.setSpacing(10);
        sendMessageBox.setAlignment(Pos.CENTER);

        final VBox sendReceiveMessageBox = new VBox(receiveMessagesListView_, sendMessageBox);
        sendReceiveMessageBox.setSpacing(15);
        GridPane.setHgrow(sendReceiveMessageBox, Priority.ALWAYS);
        GridPane.setVgrow(sendReceiveMessageBox, Priority.ALWAYS);

        final Label connectedUsersLabel = new Label("Connected users:");
        connectedUsersLabel.setFont(new Font(15));

        connectedUsersListView_ = new ListView<>();
        connectedUsersListView_.setEditable(false);
        connectedUsersListView_.setFocusTraversable(false);
        connectedUsersListView_.setPlaceholder(new Label("No other users" + System.lineSeparator() + "are connected"));
        connectedUsersListView_.setMinWidth(Constants.DEFAULT_WINDOW_SIZE[0] / 5);
        connectedUsersListView_.setCellFactory(param -> new UserInfoListCell());
        VBox.setVgrow(connectedUsersListView_, Priority.ALWAYS);

        final VBox rightBox = new VBox(connectedUsersLabel, connectedUsersListView_);
        rightBox.setSpacing(15);
        GridPane.setVgrow(rightBox, Priority.ALWAYS);

        final GridPane userPane = new GridPane();
        userPane.setPadding(new Insets(20));
        userPane.setHgap(15);
        GridPane.setHgrow(userPane, Priority.ALWAYS);
        GridPane.setVgrow(userPane, Priority.ALWAYS);
        userPane.add(menuBar, 0, 0);
        userPane.add(sendReceiveMessageBox, 0, 1);
        userPane.add(rightBox, 1, 1);

        final GridPane mainPane = new GridPane();
        mainPane.add(menuBar, 0, 0);
        mainPane.add(userPane, 0, 1);

        scene_ = new Scene(mainPane, Constants.DEFAULT_WINDOW_SIZE[0], Constants.DEFAULT_WINDOW_SIZE[1]);
        scene_.getStylesheets().add(Objects.requireNonNull(MainScene.class.getClassLoader().getResource("style.css")).toExternalForm()); // style the ListView
    }

    /**
     * Receives the messages from the server and handles it (by putting it on the screen for example).
     *
     * @param message the message to handle (should be a <code>Message</code> if everything is working right)
     * @see Message
     */
    public void receiveMessage(final Object message) {
        if (message instanceof ConnectedUsersMessage) {
            final ConnectedUsersMessage connectedUsersMessage = (ConnectedUsersMessage) message;
            connectedUsersListView_.getItems().removeAll(); // clears the list of connected users
            for (final UserInfo user : connectedUsersMessage.getConnectedUsers()) {
                connectedUsersListView_.getItems().add(user);
            }
        } else if (message instanceof UserActionMessage) {
            final UserActionMessage userActionMessage = (UserActionMessage) message;
            receiveMessagesListView_.getItems().add(userActionMessage);
            Platform.runLater(() -> receiveMessagesListView_.scrollTo(receiveMessagesListView_.getItems().size()));
            if (userActionMessage.getAction() == UserActionMessage.UserAction.USER_CONNECTED) {
                Platform.runLater(() -> connectedUsersListView_.getItems().add(userActionMessage.getUser()));
            } else if (userActionMessage.getAction() == UserActionMessage.UserAction.USER_QUIT) {
                Platform.runLater(() -> connectedUsersListView_.getItems().remove(userActionMessage.getUser()));
            }
        } else if (message instanceof ChatMessage) {
            final ChatMessage chatMessage = (ChatMessage) message;
            if (!blockedUsers_.contains(chatMessage.getSender())) { // check if user is blocked
                receiveMessagesListView_.getItems().add(chatMessage);
                Platform.runLater(() -> receiveMessagesListView_.scrollTo(receiveMessagesListView_.getItems().size()));
                showChatMessageNotification(chatMessage);
            }
        }
    }

    /**
     * Shows a <code>TextMessage</code> in the shape of a notification on the screen when the main <code>Stage</code>
     * is not focused.
     *
     * @see TextMessage
     * @see Stage#isFocused()
     */
    private void showChatMessageNotification(final ChatMessage chatMessage) { // TODO update javadoc
        Platform.runLater(() -> {
            if (!windowData_.getStage().isFocused()) {
                if (chatMessage instanceof TextMessage) {
                    final TextMessage textMessage = (TextMessage) chatMessage;
                    String messageText = textMessage.getMessage();
                    if (messageText.length() > 300) {
                        messageText = messageText.substring(0, 300);
                        messageText = messageText.trim();
                        messageText += "...";
                    }
                    Notifications.create()
                            .title(textMessage.getSender().getName())
                            .text(WordUtils.wrap(messageText, 75))
                            .threshold(4, Notifications.create()
                                    .title(Constants.PROGRAM_NAME)
                                    .text("You've got 5 or more new messages"))
                            .show();
                } else if (chatMessage instanceof FileInfoMessage) {
                    final FileInfoMessage fileInfoMessage = (FileInfoMessage) chatMessage;
                    final String icon;
                    if (fileInfoMessage.getFileType() == FileType.IMAGE) {
                        icon = "\uD83D\uDCF7 ";
                    } else {
                        icon = "\uD83D\uDCC4 ";
                    }
                    String fileName = fileInfoMessage.getFileName();
                    if (fileName.length() > 300) {
                        fileName = fileName.substring(0, 300);
                        fileName = fileName.trim();
                        fileName += "...";
                    }
                    Notifications.create()
                            .title(fileInfoMessage.getSender().getName())
                            .text(icon + WordUtils.wrap(fileName, 75, null, true))
                            .threshold(4, Notifications.create()
                                    .title(Constants.PROGRAM_NAME)
                                    .text("You've got 5 or more new messages"))
                            .show();
                }
            }
        });
    }

    /**
     * Sends a <code>ChatMessage</code> to the server.
     *
     * @param messageText the text for the <code>ChatMessage</code>
     * @see ChatMessage
     */
    private void sendMessage(String messageText) {
        messageText = messageText.trim();
        if (!messageText.isBlank() || attachedFile_ != null) {
            try {
                if (attachedFile_ == null) {
                    final TextMessage textMessage = new TextMessage(Data.chatClientGui_.getUserInfo(), messageText);
                    Data.objectOutputStream_.writeObject(textMessage);
                    receiveMessagesListView_.getItems().add(textMessage);
                } else {
                    if (messageText.isBlank()) {
                        messageText = null;
                    }
                    final FileType fileType;
                    if (Constants.SUPPORTED_IMAGE_EXTENSIONS.contains(FilenameUtils.getExtension(attachedFile_.getName()))) {
                        fileType = FileType.IMAGE;
                    } else {
                        fileType = FileType.OTHER;
                    }
                    final FileMessage fileMessage = new FileMessage(Data.chatClientGui_.getUserInfo(), attachedFile_, fileType, messageText);
                    attachedFile_ = null;
                    attachButton_.setTooltip(new Tooltip("Attach a file"));
                    attachButton_.setGraphic(attachGlyph_);
                    Data.objectOutputStream_.writeObject(fileMessage);
                    receiveMessagesListView_.getItems().add(fileMessage.getFileInfo());
                }
                Platform.runLater(() -> receiveMessagesListView_.scrollTo(receiveMessagesListView_.getItems().size()));
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
    }

    /**
     * This method switches to the home scene.
     *
     * @param windowData information of the current window
     */
    @Override
    public void start(final WindowData windowData) {
        sendMessageLabel_.setText(Data.chatClientGui_.getUserInfo().getName() + ":");
        sendMessageLabel_.setTextFill(Data.chatClientGui_.getUserInfo().getColor());
        windowData.getStage().setScene(scene_);
        sendMessageTextField_.requestFocus();
        receiveMessagesListView_.getItems().clear();
        receiveMessagesListView_.getItems().add(new InternalMessage("Welcome to the chat!"));
        Platform.runLater(() -> receiveMessagesListView_.scrollTo(receiveMessagesListView_.getItems().size()));
    }

    /**
     * This method is implemented of <code>ChattyScene</code>, but in this case it's empty.
     *
     * @param windowData information of the current window
     */
    @Override
    public void next(final WindowData windowData) {
    }

    /**
     * Handles a cell for a <code>Message</code>. It extends <code>ListCell</code>. It looks at the message
     * and creates a <code>HBox</code> for it, which contains possible the sender and the text of the message.
     *
     * @author Thijzert
     * @see Message
     * @see ListCell
     * @see HBox
     */
    private static final class MessageListCell extends ListCell<Message> {
        private final WindowData windowData_;

        /**
         * Initializes the cell. It requires an instance of <code>WindowData</code>, because it needs its width property.
         *
         * @param windowData the data of the current window
         * @see WindowData
         * @see Window#widthProperty()
         */
        private MessageListCell(final WindowData windowData) {
            windowData_ = windowData;
        }

        /**
         * Updates the cell.
         *
         * @param message the new item for the cell
         * @param empty   Whether this cell represents data from the list. If it
         *                is empty, then it does not represent any domain data, but is a cell
         *                being used to render an "empty" row.
         */
        @Override
        public void updateItem(final Message message, final boolean empty) {
            super.updateItem(message, empty);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            if (message != null && !empty) {
                final HBox messageContentBox = new HBox();
                messageContentBox.setSpacing(5);
                messageContentBox.setPadding(new Insets(7.5));
                HBox.setHgrow(messageContentBox, Priority.NEVER);
                final MenuItem hideMessageMenuItem = new MenuItem("Hide message");
                final MenuItem blockUserMenuItem = new MenuItem();
                final ContextMenu chatMessageContextMenu = new ContextMenu(hideMessageMenuItem);
                final HBox messageBox = new HBox(messageContentBox);
                if (!(message instanceof InternalMessage || message instanceof UserActionMessage)) {
                    messageContentBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(7.5), null)));
                    messageContentBox.setBorder(messageBorder_);
                }
                if (message instanceof InternalMessage) {
                    final InternalMessage internalMessage = (InternalMessage) message;
                    final Label messageLabel = new Label(internalMessage.getMessage());
                    messageLabel.setWrapText(true);
                    messageLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_TEXT_MESSAGE_WIDTH_FORMULA);
                    windowData_.getStage().widthProperty().addListener((observable, oldValue, newValue) -> messageLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_TEXT_MESSAGE_WIDTH_FORMULA));
                    messageLabel.setFont(Font.font(Constants.DEFAULT_FONT_FAMILY, FontPosture.ITALIC, Constants.DEFAULT_FONT_SIZE));
                    messageContentBox.getChildren().add(messageLabel);
                    messageBox.setAlignment(Pos.CENTER);
                } else if (message instanceof UserActionMessage) {
                    final UserActionMessage userActionMessage = (UserActionMessage) message;
                    final Label messageLabel = new Label(userActionMessage.toString());
                    messageLabel.setWrapText(true);
                    messageLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_TEXT_MESSAGE_WIDTH_FORMULA);
                    windowData_.getStage().widthProperty().addListener((observable, oldValue, newValue) -> messageLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_TEXT_MESSAGE_WIDTH_FORMULA));
                    messageLabel.setFont(Font.font(Constants.DEFAULT_FONT_FAMILY, FontPosture.ITALIC, Constants.DEFAULT_FONT_SIZE));
                    messageContentBox.getChildren().add(messageLabel);
                    messageBox.setAlignment(Pos.CENTER);
                } else if (message instanceof ChatMessage) {
                    final ChatMessage chatMessage = (ChatMessage) message;
                    final Label senderLabel = new Label(chatMessage.getSender().getName());
                    senderLabel.setFont(Font.font(Constants.DEFAULT_FONT_FAMILY, FontWeight.BOLD, Constants.DEFAULT_FONT_SIZE));
                    senderLabel.setTextFill(chatMessage.getSender().getColor());
                    if (messageIsOfSelf(message)) {
                        messageBox.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        messageContentBox.getChildren().add(senderLabel);
                        blockUserMenuItem.setText("Block " + chatMessage.getSender().getName());
                        blockUserMenuItem.setOnAction(event -> blockedUsers_.add(chatMessage.getSender()));
                        chatMessageContextMenu.getItems().add(blockUserMenuItem);
                    }
                    if (chatMessage instanceof TextMessage) {
                        final TextMessage textMessage = (TextMessage) chatMessage;
                        final Label messageLabel = new Label(textMessage.getMessage());
                        if (messageIsOfSelf(textMessage)) {
                            messageLabel.setFont(Font.font(Constants.DEFAULT_FONT_FAMILY, FontWeight.BOLD, Constants.DEFAULT_FONT_SIZE));
                        }
                        messageLabel.setWrapText(true);
                        messageLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_TEXT_MESSAGE_WIDTH_FORMULA);
                        windowData_.getStage().widthProperty().addListener((observable, oldValue, newValue) -> messageLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_TEXT_MESSAGE_WIDTH_FORMULA));
                        messageContentBox.getChildren().add(messageLabel);
                    } else if (chatMessage instanceof FileInfoMessage) {
                        final FileInfoMessage fileInfoMessage = (FileInfoMessage) chatMessage;
                        final MenuItem downloadItem = new MenuItem("Download file...");
                        downloadItem.setOnAction(event -> {
                            final FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Download file");
                            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                            if (Files.exists(new File(System.getProperty("user.home") + "/Downloads").toPath())) {
                                fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
                            } else {
                                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                            }
                            fileChooser.setInitialFileName(fileInfoMessage.getFileName());
                            final File fileToDownload = fileChooser.showSaveDialog(windowData_.getStage());
                            if (fileToDownload != null) {
                                // TODO DOWNLOAD FILE
                                downloadFile(fileInfoMessage, fileToDownload);
                            }
                        });
                        chatMessageContextMenu.getItems().add(0, downloadItem);
                        if (fileInfoMessage.getFileType() == FileType.IMAGE) {
                            final Image image = new Image(""); // TODO READ FILE
                            final ImageView imageView = new ImageView(image);
                            imageView.setPreserveRatio(true);
                            imageView.setFitWidth(getListView().getWidth() * Constants.MAX_IMAGE_MESSAGE_SIZE_FORMULA);
                            final Label captionLabel = new Label(fileInfoMessage.getCaption());
                            if (messageIsOfSelf(fileInfoMessage)) {
                                captionLabel.setFont(Font.font(Constants.DEFAULT_FONT_FAMILY, FontWeight.BOLD, Constants.DEFAULT_FONT_SIZE));
                            }
                            captionLabel.setWrapText(true);
                            captionLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_IMAGE_MESSAGE_SIZE_FORMULA);
                            windowData_.getStage().widthProperty().addListener((observable, oldValue, newValue) -> {
                                imageView.setFitWidth(getListView().getWidth() * Constants.MAX_IMAGE_MESSAGE_SIZE_FORMULA);
                                captionLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_IMAGE_MESSAGE_SIZE_FORMULA);
                            });
                            final VBox imageBox = new VBox(imageView);
                            if (fileInfoMessage.getCaption() != null) {
                                imageBox.getChildren().add(captionLabel);
                            }
                            imageBox.setSpacing(7.5);
                            messageContentBox.getChildren().add(imageBox);
                            downloadItem.setText("Download image...");
                        } else if (fileInfoMessage.getFileType() == FileType.OTHER) {
                            final long fileSizeBytes = fileInfoMessage.getFileSize();
                            final String fileSize;
                            if (fileSizeBytes >= 1000000000) {
                                fileSize = String.format("%.1f", fileSizeBytes / 100000.0) + " gB";
                            } else if (fileSizeBytes >= 1000000) {
                                fileSize = String.format("%.1f", fileSizeBytes / 10000.0) + " mB";
                            } else if (fileSizeBytes >= 1000) {
                                fileSize = String.format("%.1f", fileSizeBytes / 1000.0) + " kB";
                            } else {
                                fileSize = fileSizeBytes + " bytes";
                            }

                            final Label fileNameLabel = new Label(fileInfoMessage.getFileName());
                            fileNameLabel.setWrapText(true);
                            fileNameLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_FILE_MESSAGE_SIZE_FORMULA);
                            final Label fileSizeLabel = new Label(fileSize);
                            fileSizeLabel.setOpacity(0.5);
                            final Button downloadButton = new Button("Download...");
                            downloadButton.setMaxWidth(Double.MAX_VALUE);
                            downloadButton.setOnAction(event -> downloadItem.fire());
                            final VBox fileBox = new VBox(new VBox(fileNameLabel, fileSizeLabel), downloadButton);
                            fileBox.setPadding(new Insets(6));
                            fileBox.setSpacing(5);
                            fileBox.setBorder(new Border(new BorderStroke(Color.web("#000000", 0.25), BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(1))));
                            final Label captionLabel = new Label(fileInfoMessage.getCaption());
                            captionLabel.setWrapText(true);
                            captionLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_FILE_MESSAGE_SIZE_FORMULA);
                            windowData_.getStage().widthProperty().addListener((observable, oldValue, newValue) -> {
                                fileNameLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_FILE_MESSAGE_SIZE_FORMULA);
                                captionLabel.setMaxWidth(getListView().getWidth() * Constants.MAX_FILE_MESSAGE_SIZE_FORMULA);
                            });
                            final VBox fileCaptionBox = new VBox(3, fileBox);
                            if (fileInfoMessage.getCaption() != null) {
                                fileCaptionBox.getChildren().add(captionLabel);
                            }
                            messageContentBox.getChildren().add(fileCaptionBox);
                        }
                    }
                    messageBox.setOnContextMenuRequested(event -> chatMessageContextMenu.show(messageBox.getScene().getWindow(), event.getScreenX(), event.getScreenY()));
                    hideMessageMenuItem.setOnAction(event -> getListView().getItems().remove(getItem()));
                }
                Platform.runLater(() -> setGraphic(messageBox));
            } else {
                Platform.runLater(() -> setGraphic(null)); // to avoid a bug in javafx 11
            }
        }

        /**
         * Checks if a message is of the running client itself.
         *
         * @param message the message to check
         * @return whether a message is of the running client itself
         * @see ChatClient
         */
        private boolean messageIsOfSelf(final Message message) {
            if (message instanceof ChatMessage) {
                final ChatMessage chatMessage = (ChatMessage) message;
                return chatMessage.getSender().equals(Data.chatClientGui_.getUserInfo());
            }
            return false;
        }

        private void downloadFile(final FileInfoMessage fileInfoMessage, final File fileLocation) { // TODO javadoc
            try (final FileOutputStream fileOutputStream = new FileOutputStream(fileLocation)) {
                Data.objectOutputStream_.writeObject(new FileContentRequestMessage(Data.chatClientGui_.getUserInfo(), fileInfoMessage));
                long timesToRead = fileInfoMessage.getFileSize() / Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH;
                if (fileInfoMessage.getFileSize() % Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH != 0) {
                    timesToRead += 1;
                }
                for (int i = 0; i < timesToRead; i++) {
                    final Object filePartObject = Data.objectInputStream_.readObject();
                    if (filePartObject instanceof FilePartMessage) {
                        final FilePartMessage filePart = (FilePartMessage) filePartObject;
                        byte[] filePartBytes = filePart.getFilePartBytes();
                        if (i == timesToRead - 1) {
                            filePartBytes = Arrays.copyOf(filePartBytes, (int) (Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH - (fileInfoMessage.getFileSize() - Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH * (timesToRead - 1))));
                        }
                        fileOutputStream.write(filePartBytes);
                    } else {
                        break;
                    }
                }
            } catch (final IOException | ClassNotFoundException exception) {
                ProgramFunctions.showThrowableAlert(exception);
            }
        }
    }

    /**
     * Handles a cell for a <code>UserInfo</code>. It extends <code>ListCell</code>. It creates text with the user's color.
     * If the list of blocked users changes, it checks if it should add a warning sign to the cell of the blocked user.
     *
     * @author Thijzert
     * @see UserInfo
     * @see ListCell
     */
    private static final class UserInfoListCell extends ListCell<UserInfo> {
        /**
         * Updates the cell.
         *
         * @param userInfo the new item for the cell
         * @param empty    Whether this cell represents data from the list. If it
         *                 is empty, then it does not represent any domain data, but is a cell
         *                 being used to render an "empty" row.
         */
        @Override
        public void updateItem(final UserInfo userInfo, final boolean empty) {
            super.updateItem(userInfo, empty);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            if (userInfo != null && !empty) {
                final Label nameLabel = new Label(userInfo.getName());
                nameLabel.setTextFill(userInfo.getColor());
                nameLabel.setFont(Font.font(Constants.DEFAULT_FONT_FAMILY, FontWeight.BOLD, Constants.DEFAULT_FONT_SIZE));
                nameLabel.setWrapText(true);
                nameLabel.setContentDisplay(ContentDisplay.LEFT);

                final Glyph blockedGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.EXCLAMATION_TRIANGLE);
                blockedGlyph.setColor(Color.web("#FD9400"));
                blockedGlyph.setFontSize(10.5);
                blockedUsers_.addListener((ListChangeListener<UserInfo>) change -> {
                    if (change.getList().contains(userInfo)) {
                        nameLabel.setGraphic(blockedGlyph);
                    } else {
                        nameLabel.setGraphic(null);
                    }
                });

                setOnContextMenuRequested(event1 -> {
                    final ContextMenu contextMenu = new ContextMenu();
                    final MenuItem blockUser = new MenuItem("Block " + userInfo.getName());
                    blockUser.setOnAction(event2 -> {
                        blockedUsers_.add(userInfo);
                        setTooltip(new Tooltip(userInfo.getName() + " is blocked"));
                    });
                    final MenuItem unblockUser = new MenuItem("Unblock " + userInfo.getName());
                    unblockUser.setOnAction(event2 -> {
                        blockedUsers_.remove(userInfo);
                        setTooltip(null);
                    });
                    if (blockedUsers_.contains(userInfo)) {
                        contextMenu.getItems().add(unblockUser);
                    } else {
                        contextMenu.getItems().add(blockUser);
                    }
                    contextMenu.show(getScene().getWindow(), event1.getScreenX(), event1.getScreenY());
                });

                Platform.runLater(() -> setGraphic(nameLabel));
            } else {
                Platform.runLater(() -> setGraphic(null)); // to avoid a bug in javafx 11 // TODO see if this is still needed in JavaFX 21
            }
        }
    }
}
