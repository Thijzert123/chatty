package thijzert.chatty.client.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import thijzert.chatty.client.ChatClient;
import thijzert.chatty.client.gui.scene.ChattyScene;
import thijzert.chatty.client.gui.scene.ConnectScene;
import thijzert.chatty.client.gui.scene.MainScene;
import thijzert.chatty.data.Constants;
import thijzert.chatty.data.Data;
import thijzert.chatty.data.UserInfo;
import thijzert.chatty.data.WindowData;
import thijzert.chatty.functions.ProgramFunctions;

/**
 * Main class for the Chat Client Gui. It extends <code>javafx.application.Application</code>. After the stage is set,
 * the program will immediately go over to <code>MainScene</code>, which sets the scene to the main scene.
 *
 * @author Thijzert
 * @see Application
 * @see MainScene
 */
public final class ChatClientGui extends Application implements ChatClient {
    private UserInfo userInfo_;

    /**
     * Overrides <code>start</code> method of <code>Application</code>. It creates the <code>WindowData</code>,
     * which is needed for the <code>MainScene</code> to start. The program switches to the main scene by
     * calling <code>ProgramFunctions.startScene(...)</code>.
     *
     * @param stage stage given by <code>Application</code>
     * @see Application
     * @see MainScene
     * @see ProgramFunctions#startScene(ChattyScene, WindowData)
     */
    @Override
    public void start(final Stage stage) {
        Data.chatClientGui_ = this;
        Data.windowData_ = new WindowData(stage, Constants.DEFAULT_WINDOW_SIZE[0], Constants.DEFAULT_WINDOW_SIZE[1]);

        stage.setTitle(Constants.PROGRAM_NAME);
//        stage.getIcons().add(new Image(ChatClientGui.class.getClassLoader().getResourceAsStream(Constants.ICON_RESOURCE_NAME))); TODO get icon
//        stage.setMinWidth(Constants.DEFAULT_WINDOW_SIZE[0] * Constants.MIN_WINDOW_SIZE_FORMULA);
//        stage.setMinHeight(Constants.DEFAULT_WINDOW_SIZE[1] * Constants.MIN_WINDOW_SIZE_FORMULA);
        stage.show();

        Data.connectScene_ = new ConnectScene(Data.windowData_);
        Data.mainScene_ = new MainScene(Data.windowData_);
        ProgramFunctions.startScene(Data.connectScene_, Data.windowData_);
    }

    /**
     * Main method for <code>ChatClientGui</code>. Can be called by <code>ChatClientGuiLauncher</code>,
     * to avoid a bug in JavaFX 11. This method calls <code>Application.launch(...)</code>.
     *
     * @param args arguments for the program
     * @see ChatClientGuiLauncher
     * @see ChatClientGuiLauncher#main(String[])
     * @see Application#launch(String...)
     */
    public static void main(final String[] args) {
        launch(args);
    }

    /**
     * Returns the <code>UserInfo</code>.
     *
     * @return the <code>UserInfo</code>
     * @see UserInfo
     */
    public UserInfo getUserInfo() {
        return userInfo_;
    }

    /**
     * Sets the <code>UserInfo</code>.
     *
     * @param userInfo the new <code>UserInfo</code>
     * @see UserInfo
     */
    public void setUserInfo(final UserInfo userInfo) {
        userInfo_ = userInfo;
    }
}
