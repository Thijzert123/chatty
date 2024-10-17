package thijzert.chatty.message;

import thijzert.chatty.data.UserInfo;

import java.util.Set;

/**
 * A message that contains a <code>Set</code> with all the connected usernames.
 *
 * @author Thijzert
 * @see Set
 */
public final class ConnectedUsersMessage implements Message {
    private static final long serialVersionUID = -8825977639925923301L;
    private final Set<UserInfo> connectedUsers_;

    /**
     * Initializes the message.
     *
     * @param connectedUsers a <code>Set</code> with all the connected users
     * @see Set
     */
    public ConnectedUsersMessage(final Set<UserInfo> connectedUsers) {
        connectedUsers_ = connectedUsers;
    }

    /**
     * Returns all the connected users.
     *
     * @return all the connected users
     */
    public Set<UserInfo> getConnectedUsers() {
        return connectedUsers_;
    }

    /**
     * Returns a summary of the connected users in a fluent English sentence.
     *
     * @return a summary of the connected users in a fluent English sentence
     */
    @Override
    public String toString() {
        if (connectedUsers_.size() > 0) {
            return "Connected users: " + connectedUsers_;
        } else {
            return "No other users are connected";
        }
    }
}
