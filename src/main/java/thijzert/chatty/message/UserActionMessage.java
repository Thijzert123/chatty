package thijzert.chatty.message;

import thijzert.chatty.data.UserInfo;

/**
 * A server message that tells about an action a user did (connecting to the server or quitting).
 *
 * @author Thijzert
 */
public final class UserActionMessage implements Message {
    private static final long serialVersionUID = -1775667194327390235L;
    private final UserInfo user_;
    private final UserAction userAction_;

    /**
     * The actions a user can do.
     */
    public enum UserAction {
        /**
         * A user is connected.
         */
        USER_CONNECTED,
        /**
         * A user quit.
         */
        USER_QUIT
    }

    /**
     * This constructor initializes the <code>UserActionMessage</code>.
     *
     * @param user       the user this message is about
     * @param userAction the action the user executed
     */
    public UserActionMessage(final UserInfo user, final UserAction userAction) {
        user_ = user;
        userAction_ = userAction;
    }

    /**
     * Returns the user this message is about.
     *
     * @return the user this message is about
     */
    public UserInfo getUser() {
        return user_;
    }

    /**
     * Returns the action this message is about.
     *
     * @return the action this message is about
     */
    public UserAction getAction() {
        return userAction_;
    }

    /**
     * Returns a summary of the <code>UserActionMessage</code>.
     *
     * @return a summary of the <code>UserActionMessage</code>
     */
    @Override
    public String toString() {
        if (userAction_ == UserAction.USER_CONNECTED) {
            return "New user connected: " + user_.getName();
        } else if (userAction_ == UserAction.USER_QUIT) {
            return user_.getName() + " has quit";
        }
        return null;
    }
}
