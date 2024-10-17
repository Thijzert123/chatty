package thijzert.chatty.server;

import thijzert.chatty.data.Constants;
import thijzert.chatty.data.Data;
import thijzert.chatty.data.UserInfo;
import thijzert.chatty.message.*;

import java.io.*;
import java.net.Socket;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author Thijzert
 * @see Thread
 * @see ChatServer
 */
final class UserThread extends Thread {
    private final Socket socket_;
    private final ChatServer server_;
    private ObjectOutputStream objectOutputStream_;

    /**
     * Initializes the thread.
     *
     * @param socket the socket currently used
     * @param server the server currently used
     */
    UserThread(final Socket socket, final ChatServer server) {
        socket_ = socket;
        server_ = server;
    }

    /**
     * This function overrides the run method in <code>Thread</code>. It reads the user input and broadcasts it to
     * the other users.
     *
     * @see Thread
     */
    @Override
    public void run() {
        UserInfo userInfo = new UserInfo("unknown");
        try {
            final OutputStream outputStream = socket_.getOutputStream();
            objectOutputStream_ = new ObjectOutputStream(outputStream);
            objectOutputStream_.flush();

            final InputStream inputStream = socket_.getInputStream();
            final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            sendMessage(new ConnectedUsersMessage(server_.getConnectedUsers()));

            final Object userInfoObject = objectInputStream.readObject();
            if (userInfoObject instanceof UserInfo) {
                userInfo = (UserInfo) userInfoObject;
            } else {
                System.err.println("Can't resolve userinfo");
                return;
            }

            server_.addUserName(userInfo);

            System.out.println("New user connected: " + userInfo.getName());
            server_.broadcast(new UserActionMessage(userInfo, UserActionMessage.UserAction.USER_CONNECTED), this);

            ChatMessage clientMessage;
            do {
                final Object clientMessageObject = objectInputStream.readObject();
                if (clientMessageObject instanceof FileMessage) {
                    clientMessage = null;
                    final FileMessage fileMessage = (FileMessage) clientMessageObject;
                    server_.broadcast(fileMessage.getFileInfo(), this);
                } else if (clientMessageObject instanceof FileContentRequestMessage) {
                    clientMessage = null;
                    final FileContentRequestMessage fileContentRequestMessage = (FileContentRequestMessage) clientMessageObject;
//                    try (final RandomAccessFile fileData = new RandomAccessFile(Data.serverTempDir_.resolve(String.valueOf(fileContentRequestMessage.getRequestedMessage().getId())).toFile(), "r")) {
//                        final byte[] byteBuffer = new byte[Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH];
//                        for (long i = 0, length = fileData.length() / Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH; i < length; i++) {
//                            fileData.readFully(byteBuffer);
//                            sendMessage(new FilePartMessage(byteBuffer));
//                        }
//                    }
                    final File fileToWrite = new File(Data.serverTempDir_.toString() + File.separator + fileContentRequestMessage.getRequestedMessage().getId());
                    final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileToWrite));
                    final byte[] byteBuffer = new byte[Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH];
                    long timesToWrite = fileToWrite.length() / Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH;
                    if (fileToWrite.length() % Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH != 0) {
                        timesToWrite += 1;
                    }
                    for (long i = 0; i < timesToWrite; i++) {
                        if (bufferedInputStream.read(byteBuffer) != -1) {
                            sendMessage(new FilePartMessage(byteBuffer));
                        }
                    }
                    bufferedInputStream.close();
                } else if (clientMessageObject instanceof ChatMessage) {
                    clientMessage = (ChatMessage) clientMessageObject;
                    server_.broadcast(clientMessage, this);
                } else {
                    break;
                }
            } while (!chatMessageIsBye(clientMessage));

            server_.removeUser(userInfo, this);
            socket_.close();

            server_.broadcast(new UserActionMessage(userInfo, UserActionMessage.UserAction.USER_QUIT), this);
        } catch (final EOFException eofException) {
            server_.removeUser(userInfo, this);
            try {
                socket_.close();
            } catch (final IOException ioException) {
                System.err.println("Error in userthread: " + ioException.getMessage());
                ioException.printStackTrace();
            }
            server_.broadcast(new UserActionMessage(userInfo, UserActionMessage.UserAction.USER_QUIT), this);
        } catch (final InvalidClassException invalidClassException) { // TODO check if this works
            server_.removeUser(userInfo, this);
            try {
                socket_.close();
            } catch (final IOException ioException) {
                System.err.println("Error in userthread: " + ioException.getMessage());
                ioException.printStackTrace();
            }
        } catch (final IOException ioException) {
            System.err.println("Error in userthread: " + ioException.getMessage());
            ioException.printStackTrace();
        } catch (final ClassNotFoundException classNotFoundException) {
            System.err.println("Class not found: " + classNotFoundException.getMessage());
            classNotFoundException.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param message the message that is sent to the client
     */
    void sendMessage(final Message message) {
        try {
            objectOutputStream_.writeObject(message);
            objectOutputStream_.flush();
        } catch (final IOException ioException) {
            System.err.println("Can't write to user: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    /**
     * Checks if the chat message equals 'bye' (that means the client quits).
     *
     * @param chatMessage the message that needs to be checked
     * @return true if the message is 'bye'
     */
    private boolean chatMessageIsBye(final ChatMessage chatMessage) {
        if (chatMessage instanceof TextMessage) {
            return ((TextMessage) chatMessage).getMessage().equals("bye");
        }
        return false;
    }
}