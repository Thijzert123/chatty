package thijzert.chatty.data;

import thijzert.chatty.client.gui.ChatClientGui;
import thijzert.chatty.client.gui.scene.ConnectScene;
import thijzert.chatty.client.gui.scene.MainScene;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

/**
 * Collection of the data of the program. These are the same as in <code>Constants</code>,
 * however, these are changeable.
 *
 * @author Thijzert
 */
public class Data {
    public static Path serverTempDir_ = null; // TODO javadoc

    /**
     * The <code>ConnectScene</code>.
     *
     * @see ConnectScene
     */
    public static ConnectScene connectScene_ = null;
    /**
     * The <code>MainScene</code>.
     *
     * @see MainScene
     */
    public static MainScene mainScene_ = null;
    /**
     * The <code>ChatClientGui</code>.
     *
     * @see ChatClientGui
     */
    public static ChatClientGui chatClientGui_ = null;
    /**
     * The <code>ObjectOutputStream</code>. Here a client can write anything to.
     *
     * @see ObjectOutputStream
     */
    public static ObjectOutputStream objectOutputStream_ = null;
    public static ObjectInputStream objectInputStream_ = null; // TODO javadoc
    /**
     * The <code>WindowData</code> of a window.
     *
     * @see WindowData
     */
    public static WindowData windowData_ = null;
}
