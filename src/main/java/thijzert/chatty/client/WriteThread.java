package thijzert.chatty.client;

import thijzert.chatty.message.TextMessage;

import java.io.Console;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author Thijzert
 */
final class WriteThread extends Thread {
    private final Socket socket_;
    private final ChatClientLite client_;
    private ObjectOutputStream objectOutputStream_;

    /**
     * This constructor initializes the write thread. It creates an <code>OutputStream</code>, which will then be used
     * for an <code>ObjectOutputStream</code>.
     *
     * @param socket the socket currently used
     * @param client the chatclient
     * @see Socket
     * @see ChatClientLite
     * @see OutputStream
     * @see ObjectOutputStream
     */
    WriteThread(final Socket socket, final ChatClientLite client) {
        socket_ = socket;
        client_ = client;

        try {
            final OutputStream output = socket.getOutputStream();
            objectOutputStream_ = new ObjectOutputStream(output);
        } catch (final IOException ioException) {
            System.err.println("Error getting output stream: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    /**
     * This function overrides the run method in <code>Thread</code>. It reads the command line input from the user,
     * and then it sends it to the server.
     *
     * @see Thread
     */
    @Override
    public void run() {
        try {
            final Console console = System.console();

            objectOutputStream_.writeObject(client_.getUserInfo());

            String text;
            do {
                text = console.readLine("[" + client_.getUserInfo().getName() + "]: ").trim();
                if (!text.isBlank()) {
                    objectOutputStream_.writeObject(new TextMessage(client_.getUserInfo(), text));
                }
            } while (!text.equals("bye"));

            socket_.close();
        } catch (final IOException ioException) {
            System.err.println("Error writing to server: " + ioException.getMessage());
        }
    }
}
