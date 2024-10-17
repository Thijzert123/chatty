package thijzert.chatty.data;

import javafx.stage.Stage;

/**
 * A collection of the data of a window.
 *
 * @author Thijzert
 */
public class WindowData {
    private Stage stage_;
    private double width_;
    private double height_;

    /**
     * Initializes the window data.
     *
     * @param stage  the stage that belongs to the window
     * @param width  width of the window
     * @param height height of the window
     * @see Stage
     */
    public WindowData(final Stage stage, final double width, final double height) {
        stage_ = stage;
        width_ = width;
        height_ = height;
    }

    /**
     * Returns the stage that belongs to the window.
     *
     * @return the stage that belongs to the window
     * @see Stage
     */
    public Stage getStage() {
        return stage_;
    }

    /**
     * Sets the stage that belongs to the window.
     *
     * @param stage the new stage
     * @see Stage
     */
    public void setStage(final Stage stage) {
        stage_ = stage;
    }

    /**
     * Returns the width of the window.
     *
     * @return the width of the window
     */
    public double getWidth() {
        return width_;
    }

    /**
     * Sets the width of the window.
     *
     * @param width the new width
     */
    public void setWidth(final double width) {
        width_ = width;
    }

    /**
     * Returns the height of the window.
     *
     * @return the height
     */
    public double getHeight() {
        return height_;
    }

    /**
     * Sets the height of the window.
     *
     * @param height the new height
     */
    public void setHeight(final double height) {
        height_ = height;
    }
}