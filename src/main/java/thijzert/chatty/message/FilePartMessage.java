package thijzert.chatty.message;

/**
 * @author Thijzert
 */
public class FilePartMessage implements Message { // TODO javadoc
    private static final long serialVersionUID = 7928213082348536804L;
    final byte[] filePartBytes_;

    public FilePartMessage(final byte[] filePartBytes) {
        filePartBytes_ = filePartBytes;
    }

    public byte[] getFilePartBytes() {
        return filePartBytes_;
    }
}
