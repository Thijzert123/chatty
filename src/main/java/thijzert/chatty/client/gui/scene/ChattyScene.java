package thijzert.chatty.client.gui.scene;

import thijzert.chatty.data.WindowData;

/**
 * Interface of all used Scenes. They have a start and next method, but next doesn't have to be used.
 *
 * @author Thijzert
 */
public interface ChattyScene {
    /**
     * This method starts the scene. It switches the stage to another scene by calling <code>stage.setScene(...)</code>.
     *
     * @param windowData information of the current window
     * @see WindowData
     * @see javafx.stage.Stage#setScene(javafx.scene.Scene)
     */
    void start(final WindowData windowData);

    /**
     * This method makes sure the current scene can safely switch to the next scene. This method doesn't have to be used.
     *
     * @param windowData information of the current window
     * @see WindowData
     */
    void next(final WindowData windowData);
}
