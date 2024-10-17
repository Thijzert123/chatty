package thijzert.chatty.client;

import javafx.scene.control.Alert;
import thijzert.chatty.client.gui.ChatClientGui;
import thijzert.chatty.data.Constants;
import thijzert.chatty.data.Data;
import thijzert.chatty.functions.ProgramFunctions;
import thijzert.chatty.message.ConnectedUsersMessage;
import thijzert.chatty.message.FileInfoMessage;
import thijzert.chatty.message.TextMessage;
import thijzert.chatty.message.UserActionMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * This thread is responsible for reading server's input and printing it
 * to the console.
 * It runs in an infinite loop until the client disconnects from the server.
 *
 * @author Thijzert
 */
public final class ReadThread extends Thread {
    private final Socket socket_;
    private final ChatClient client_;

    /**
     * This constructor initializes the read thread. It creates an <code>InputStream</code>, which will then be used
     * for an <code>ObjectInputStream</code>.
     *
     * @param socket the socket currently used
     * @param client the chatclient
     * @see Socket
     * @see ChatClient
     * @see InputStream
     * @see ObjectInputStream
     */
    public ReadThread(final Socket socket, final ChatClient client) {
        socket_ = socket;
        client_ = client;

        try {
            final InputStream inputStream = socket.getInputStream();
            Data.objectInputStream_ = new ObjectInputStream(inputStream);
        } catch (final IOException ioException) {
            if (client_ instanceof ChatClientGui) {
                ProgramFunctions.showThrowableAlert(ioException);
            } else {
                System.err.println("Error getting input stream: " + ioException.getMessage());
                ioException.printStackTrace();
            }
        }
    }

    /**
     * This function overrides the run method in <code>Thread</code>. It reads the server input and prints it
     * on the commandline.
     *
     * @see Thread
     */
    @Override
    public void run() {
        while (true) {
            try {
                final Object objectResponse = Data.objectInputStream_.readObject();

                if (client_ instanceof ChatClientGui) {
                    Data.mainScene_.receiveMessage(objectResponse);
                    continue;
                }

                if (objectResponse instanceof ConnectedUsersMessage) {
                    final ConnectedUsersMessage connectedUsersMessage = (ConnectedUsersMessage) objectResponse;
                    System.out.println(Constants.CARRIAGE_RETURN_SYMBOOL + connectedUsersMessage);
                } else if (objectResponse instanceof UserActionMessage) {
                    final UserActionMessage userActionMessage = (UserActionMessage) objectResponse;
                    System.out.println(Constants.CARRIAGE_RETURN_SYMBOOL + userActionMessage);
                } else if (objectResponse instanceof TextMessage) {
                    final TextMessage textMessage = (TextMessage) objectResponse;
                    System.out.println(Constants.CARRIAGE_RETURN_SYMBOOL + "[" + textMessage.getSender().getName() + "]: " + textMessage.getMessage());
                } else if (objectResponse instanceof FileInfoMessage) {
                    final FileInfoMessage fileInfoMessage = (FileInfoMessage) objectResponse;
                    System.out.println(Constants.CARRIAGE_RETURN_SYMBOOL + "[" + fileInfoMessage.getSender().getName() + "]: " + fileInfoMessage.getFileName());
                }

                // prints the username after displaying the server's message
                System.out.print("[" + client_.getUserInfo().getName() + "]: ");
            } catch (final SocketException socketException) {
                break; // the user said 'bye' (or quit in another way)
            } catch (final IOException ioException1) {
                if (client_ instanceof ChatClientGui) {
                    ProgramFunctions.showAlert(Alert.AlertType.ERROR, "Server is closed: can't read from server.");
                    try {
                        socket_.close();
                    } catch (final IOException ioException2) {
                        throw new RuntimeException(ioException2);
                    }
                    ProgramFunctions.startScene(Data.connectScene_, Data.windowData_);
                    break;
                } else {
                    System.err.println(System.lineSeparator() + "Server is closed: can't read from server");
                    System.exit(1);
                }
            } catch (final ClassNotFoundException classNotFoundException) {
                if (client_ instanceof ChatClientGui) {
                    ProgramFunctions.showThrowableAlert(classNotFoundException);
                } else {
                    System.err.println("Class not found: " + classNotFoundException.getMessage());
                    classNotFoundException.printStackTrace();
                }
                break;
            }
        }
    }
}
