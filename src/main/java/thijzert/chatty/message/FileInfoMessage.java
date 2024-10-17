package thijzert.chatty.message;

import thijzert.chatty.data.FileType;
import thijzert.chatty.data.UserInfo;

/**
 * @author Thijzert
 */
public class FileInfoMessage extends ChatMessage { // TODO javadoc
    private static final long serialVersionUID = 5981032247712001040L;
    private final String caption_;
    private final String fileName_;
    private final FileType fileType_;
    private final long fileSize_;
    private final int id_;

    /**
     * This constructor initializes the message. It needs the senders' info.
     *
     * @param sender the sender of the message
     */
    FileInfoMessage(final UserInfo sender, final String fileName, final FileType fileType, final long fileSize, final String caption, final int id) {
        super(sender);
        caption_ = caption;
        fileName_ = fileName;
        fileType_ = fileType;
        fileSize_ = fileSize;
        id_ = id;
    }


    public String getFileName() { // TODO javadoc
        return fileName_;
    }

    public FileType getFileType() { // TODO javadoc
        return fileType_;
    }

    public long getFileSize() {
        return fileSize_;
    }

    public String getCaption() {
        return caption_;
    }

    public int getId() {
        return id_;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof FileInfoMessage) {
            return id_ == ((FileInfoMessage) object).id_;
        }
        return false;
    }
}
