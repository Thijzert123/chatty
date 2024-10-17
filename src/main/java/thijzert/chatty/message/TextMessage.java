package thijzert.chatty.message;

import thijzert.chatty.data.UserInfo;

/**
 * This message is plain text. It extends <code>ChatMessage</code>, so this class is a <code>Serializable</code>.
 *
 * @author Thijzert
 * @see ChatMessage
 * @see java.io.Serializable
 */
public final class TextMessage extends ChatMessage {
    private static final long serialVersionUID = -4399768972907481043L;
    private final String message_;

    /**
     * This constructor initializes the text message. It sets the sender of the message and the message itself.
     *
     * @param sender  the sender of the text message
     * @param message the text message
     */
    public TextMessage(final UserInfo sender, final String message) {
        super(sender);
        message_ = message;
    }

    /**
     * Returns the text message.
     *
     * @return the text message
     */
    public String getMessage() {
        return message_;
    }
}