package thijzert.chatty.message;

import thijzert.chatty.data.UserInfo;

/**
 * @author Thijzert
 */
public class FileContentRequestMessage extends ChatMessage { // TODO javadoc
    private static final long serialVersionUID = -5196934751034829537L;
    private final FileInfoMessage requestedMessage_;

    public FileContentRequestMessage(final UserInfo sender, final FileInfoMessage requestedMessage) {
        super(sender);
        requestedMessage_ = requestedMessage;
    }

    public FileInfoMessage getRequestedMessage() {
        return requestedMessage_;
    }
}
