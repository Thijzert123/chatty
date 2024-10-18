package thijzert.chatty.server;

import org.apache.commons.cli.*;
import thijzert.chatty.data.Constants;
import thijzert.chatty.data.Data;
import thijzert.chatty.data.UserInfo;
import thijzert.chatty.message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

// TODO fix: while statement cannot complete without an exception

/**
 * This is the chat server. It creates a new <code>UserThread</code> every time a new user is connected.
 * Press Ctrl + C to terminate the program.
 *
 * @author Thijzert
 */
final class ChatServer {
    private final int port_;
    private final Set<UserInfo> connectedUsers_ = new HashSet<>();
    private final Set<UserThread> userThreads_ = new HashSet<>();

    /**
     * Initializes the server.
     *
     * @param port the port the server needs to run on
     */
    ChatServer(final int port) {
        port_ = port;
    }

    /**
     * Main method of <code>ChatServer</code>. It reads the arguments and then calls <code>execute()</code>. 5050 is the default server port.
     *
     * @param args the args for the program
     * @see #execute()
     */
    public static void main(final String[] args) { // TODO add logger system
        final Options cmdOptions = new Options();
        final Option portOption = new Option("p", "port", true, "The port the server should run on. Defaults to 5050.");
        final Option helpOption = new Option("h", "help", false, "Prints information about the usage of " + Constants.PROGRAM_NAME);
        cmdOptions.addOption(portOption);
        cmdOptions.addOption(helpOption);

        final CommandLineParser cmdParser = new DefaultParser(true);
        final HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = cmdParser.parse(cmdOptions, args);
        } catch (final ParseException parseException) {
            System.out.println(parseException.getMessage() + System.lineSeparator());
            helpFormatter.printHelp(args[0], cmdOptions);
            System.exit(1);
        }

        if (cmd.hasOption(helpOption)) {
            helpFormatter.printHelp(args[0], cmdOptions);
            System.exit(0);
        }

        int port = 5050;
        if (cmd.hasOption(portOption)) {
            try {
                port = Integer.parseInt(cmd.getOptionValue(portOption));
            } catch (final NumberFormatException numberFormatException) {
                System.err.println("Given port is not a number" + System.lineSeparator());
                helpFormatter.printHelp(args[0], cmdOptions);
                System.exit(1);
            }
        }

        final ChatServer server = new ChatServer(port);
        server.execute();
    }

    /**
     * This method starts the server. It creates an infinite loop that listens to newly connected users.
     * If a new user is connected, it creates a new <code>UserThread</code>.
     *
     * @see UserThread
     */
    private void execute() { // TODO update javadoc
        try {
            Data.serverTempDir_ = Files.createTempDirectory(Constants.PROGRAM_NAME.toLowerCase() + "-server");
            Data.serverTempDir_.toFile().deleteOnExit();
        } catch (final IOException ioException) {
            System.err.println("Can't create temp dir: " + ioException.getMessage());
            System.exit(1);
        }

        try (final ServerSocket serverSocket = new ServerSocket(port_)) {
            System.out.println("==== " + Constants.PROGRAM_NAME.toUpperCase() + " CHATSERVER ====");
            System.out.println("Version " + Constants.VERSION + ", build time " + Constants.BUILD_TIMESTAMP);
            System.out.println("Running on " + Constants.HOSTNAME + ", port " + port_);
            System.out.println(Constants.PROGRAM_NAME + " by " + Constants.AUTHOR);

            while (true) {
                final Socket socket = serverSocket.accept();
                final UserThread newUser = new UserThread(socket, this);
                userThreads_.add(newUser);
                newUser.start();
            }
        } catch (final IOException ioException) {
            System.err.println("Error in the server: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    /**
     * Delivers a message from one user to others (broadcasting).
     *
     * @param message     the message that needs to be sent
     * @param excludeUser the <code>UserThread</code> that needs to be excluded
     * @see UserThread
     */
    void broadcast(final Message message, final UserThread excludeUser) {
        for (final UserThread user : userThreads_) {
            if (user != excludeUser) {
                user.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     *
     * @param userInfo the username of the newly connected client
     */
    void addUserName(final UserInfo userInfo) {
        connectedUsers_.add(userInfo);
    }

    /**
     * When a client is disconnected, removes the associated username and UserThread.
     *
     * @param userInfo   the username that needs to be removed
     * @param userThread the <code>UserThread</code> that needs to be removed
     * @see UserThread
     */
    void removeUser(final UserInfo userInfo, final UserThread userThread) {
        final boolean removed = connectedUsers_.remove(userInfo);
        if (removed) {
            userThreads_.remove(userThread);
            System.out.println("An user quit: " + userInfo.getName());
        }
    }

    /**
     * Returns all connected usernames.
     *
     * @return the usernames
     */
    Set<UserInfo> getConnectedUsers() {
        return connectedUsers_;
    }
}