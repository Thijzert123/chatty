package thijzert.chatty.client;

import thijzert.chatty.data.Constants;
import thijzert.chatty.data.UserInfo;

import java.io.Console;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * This is the chat client program for the commandline.
 * Type 'bye' to terminate the program.
 *
 * @author Thijzert
 */
final class ChatClientLite implements ChatClient {
    private final String hostname_;
    private final int port_;
    private UserInfo userInfo_;

    /**
     * Initializes the client.
     *
     * @param hostname the hostname the client should connect to
     * @param port     the port of the hostname the client should connect to
     */
    ChatClientLite(final String hostname, final int port) {
        hostname_ = hostname;
        port_ = port;
    }

    /**
     * The main method of the chat client. It is only used for the start-method.
     * Alternative way is to call the constructor of this class.
     *
     * @param args ars given on the commandline
     */
    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println("No hostname and/or port given");
            System.exit(1);
        }

        final String hostname = args[0];
        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        } catch (final NumberFormatException numberFormatException) {
            System.err.println("Can't convert port string to a number");
            System.exit(1);
        }

        final ChatClientLite client = new ChatClientLite(hostname, port);
        client.execute();
    }

    /**
     * Starts the client. It first asks for your name, and then connects to the server.
     */
    private void execute() {
        try {
            final Console console = System.console();
            final String userName = console.readLine("Enter your name: ");
            userInfo_ = new UserInfo(userName);

            final Socket socket = new Socket();
            socket.connect(new InetSocketAddress(hostname_, port_), Constants.SOCKET_TIMEOUT_MS);

            System.out.println("Connected to the " + Constants.PROGRAM_NAME + " chatserver at " + hostname_ + " at port " + port_);

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (final UnknownHostException unknownHostException) {
            System.err.println("Server not found: " + unknownHostException.getMessage());
        } catch (final SocketTimeoutException socketTimeoutException) {
            System.err.println("Connection timed out");
        } catch (final IOException ioException) {
            System.err.println("I/O Error: " + ioException.getMessage());
        }
    }

    /**
     * Returns the userinfo of the client.
     *
     * @return the userinfo of the client
     */
    public UserInfo getUserInfo() {
        return userInfo_;
    }
}
