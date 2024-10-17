package thijzert.chatty.message;

import thijzert.chatty.data.UserInfo;

import java.io.Serializable;

/**
 * This class is a message that can be sent over the chat. It is mostly used by the client.
 * Because it needs to be sent over by <code>ObjectInputStream</code> and
 * <code>ObjectOutputStream</code>, it implements <code>Serializable</code> (indirectly).
 *
 * @author Thijzert
 * @see Serializable
 */
public class ChatMessage implements Message {
    private static final long serialVersionUID = -5758249222867657927L;
    UserInfo sender_;

    /**
     * This constructor initializes the message. It needs the senders' info.
     *
     * @param sender the sender of the message
     */
    public ChatMessage(final UserInfo sender) {
        sender_ = sender;
    }

    /**
     * Returns the sender of the message.
     *
     * @return the sender of the message
     */
    public final UserInfo getSender() {
        return sender_;
    }
}