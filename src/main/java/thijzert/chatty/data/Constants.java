package thijzert.chatty.data;

import javafx.scene.text.Font;
import thijzert.chatty.client.gui.ChatClientGui;
import thijzert.chatty.client.gui.scene.ConnectScene;
import thijzert.chatty.client.gui.scene.MainScene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The most important constants of the program. For changeable variables, see <code>Data</code>.
 *
 * @author Thijzert
 * @see Data
 * @see InetAddress
 */
public final class Constants {
    /**
     * The name of the program.
     */
    public static final String PROGRAM_NAME = "Chatty";
    /**
     * The version of Chatty. It gets the version of Chatty from the resources by calling <code>getBuildTimestamp()</code>.
     *
     * @see #getVersion()
     */
    public static final String VERSION = getVersion();
    /**
     * The build timestamp. It gets the time of when Chatty is built from the resources
     * by calling <code>getBuildTimestamp()</code>.
     *
     * @see #getBuildTimestamp()
     */
    public static final String BUILD_TIMESTAMP = getBuildTimestamp();
    /**
     * The author of Chatty.
     */
    public static final String AUTHOR = "Thijzert";

    /**
     * The default size of the window of <code>ChatClientGui</code>.
     *
     * @see ChatClientGui
     */
    public static final double[] DEFAULT_WINDOW_SIZE = {1000, 650};
    /**
     * The size of the window of <code>ChatClientGui</code> when <code>ConnectScene</code> is active.
     *
     * @see ChatClientGui
     * @see ConnectScene
     */
    public static final double[] CONNECT_WINDOW_SIZE = {800, 400};
    /**
     * Multiply x and y of <code>DEFAULT_WINDOW_SIZE</code> by this, then you'll get the minimal size of the window
     */
    public static final double MIN_WINDOW_SIZE_FORMULA = 0.75;
    /**
     * Formula used to calculate the width of a message in <code>MainScene</code>.
     *
     * @see MainScene
     */
    public static final double MAX_TEXT_MESSAGE_WIDTH_FORMULA = 0.75;
    public static final double MAX_FILE_MESSAGE_SIZE_FORMULA = 0.25; // TODO javadoc
    public static final double MAX_IMAGE_MESSAGE_SIZE_FORMULA = 0.3; // TODO javadoc

    /**
     * The default JavaFX font family.
     */
    public static final String DEFAULT_FONT_FAMILY = Font.getDefault().getFamily();
    /**
     * The default JavaFX font size.
     */
    public static final double DEFAULT_FONT_SIZE = Font.getDefault().getSize();

    /**
     * The symbool for a carriage return.
     */
    public static final String CARRIAGE_RETURN_SYMBOOL = "\r";
    public static final int DEFAULT_BYTE_ARRAY_BUFFER_LENGTH = 1048576; // TODO javadoc

    /**
     * The hostname of the machine Chatty is running on. It gets the hostname from calling <code>getHostname()</code>.
     *
     * @see #getHostname()
     */
    public static final String HOSTNAME = getHostname();
    /**
     * The default timeout for a <code>Socket</code>.
     *
     * @see java.net.Socket
     */
    public static final int SOCKET_TIMEOUT_MS = 5000;

    /**
     * The resource name of the Chatty icon.
     */
    public static final String ICON_RESOURCE_NAME = ""; // TODO get icon
    /**
     * The resource name of the build timestamp.
     *
     * @see #BUILD_TIMESTAMP
     */
    public static final String BUILD_TIMESTAMP_RESOURCE_NAME = "build-timestamp.txt";
    /**
     * The resource name of the build version.
     *
     * @see #VERSION
     */
    public static final String BUILD_VERSION_RESOURCE_NAME = "build-version.txt";
    public static final List<String> SUPPORTED_IMAGE_EXTENSIONS = Arrays.asList("bmp", "gif", "jpeg", "jpg", "png"); // TODO javadoc

    /**
     * The maximum length of a name.
     */
    public static final int MAX_NAME_LENGTH = 15;

    /**
     * Gets the hostname from <code>InetAdress</code>.
     *
     * @return the hostname of the current machine
     * @see InetAddress
     */
    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException unknownHostException) {
            return "unknown";
        }
    }

    /**
     * Gets the version from the resources.
     *
     * @return the current version
     */
    private static String getVersion() {
        try {
            return new BufferedReader(new InputStreamReader(Objects.requireNonNull(Constants.class.getClassLoader().getResourceAsStream(BUILD_VERSION_RESOURCE_NAME)), StandardCharsets.UTF_8)).readLine();
        } catch (final IOException ioException) {
            return "unknown";
        }
    }

    /**
     * Gets the build timestamp from the resources.
     *
     * @return the build time of the current version
     */
    private static String getBuildTimestamp() {
        try {
            return new BufferedReader(new InputStreamReader(Objects.requireNonNull(Constants.class.getClassLoader().getResourceAsStream(BUILD_TIMESTAMP_RESOURCE_NAME)), StandardCharsets.UTF_8)).readLine();
        } catch (final IOException ioException) {
            return "unknown";
        }
    }
}
