package thijzert.chatty.message;

/**
 * This message is used for internal feedback from (usually) a client. It only contains a <code>String</code> with the message.
 *
 * @author Thijzert
 */
public class InternalMessage implements Message {
    private static final long serialVersionUID = -1844007933161297216L;
    private final String message_;

    /**
     * Initializes the message.
     *
     * @param message a <code>String</code> of the message
     */
    public InternalMessage(final String message) {
        message_ = message;
    }

    /**
     * Returns the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message_;
    }
}
