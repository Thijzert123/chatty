package thijzert.chatty.message;

import thijzert.chatty.data.Constants;
import thijzert.chatty.data.Data;
import thijzert.chatty.data.FileType;
import thijzert.chatty.data.UserInfo;

import java.io.*;
import java.util.Random;

/**
 * @author Thijzert
 */
public class FileMessage extends ChatMessage { // TODO javadoc
    private static final long serialVersionUID = 5075932524083999700L;
    private File file_;
    private FileType fileType_;
    private String caption_;
    private FileInfoMessage fileInfo_;

    /**
     * This constructor initializes the message. It needs the senders' info and an instance of a <code>File</code>.
     * For the client to be able to know what kind of file this message contains, you need to specify the type of the file
     * with <code>FileType</code>. You can also add a caption. If this is <code>null</code>, then this message contains no caption.
     *
     * @param sender   the sender of the message
     * @param caption  the caption that comes with te file
     * @param file     the image of the message
     * @param fileType The text of the message. If <code>null</code>, no text is included.
     * @see File
     * @see FileType
     */
    public FileMessage(final UserInfo sender, final File file, final FileType fileType, final String caption) throws IOException { // TODO javadoc
        super(sender);
        file_ = file;
        fileType_ = fileType;
        caption_ = caption;
        fileInfo_ = new FileInfoMessage(getSender(), file_.getName(), fileType, file_.length(), caption, Math.abs(new Random().nextInt()));
    }

    public FileInfoMessage getFileInfo() {
        return fileInfo_;
    }

    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(fileInfo_);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file_));
        final byte[] byteBuffer = new byte[Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH];
        final long fileLength = file_.length();
        long timesToRead = fileLength / Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH;
        if (fileLength % Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH != 0) {
            timesToRead += 1;
        }
        for (long i = 0; i < timesToRead; i++) {
            if (bufferedInputStream.read(byteBuffer) != -1) {
                objectOutputStream.writeObject(new FilePartMessage(byteBuffer));
            }
        }
        bufferedInputStream.close();
        objectOutputStream.writeObject(sender_);
        objectOutputStream.writeObject(file_);
        objectOutputStream.writeObject(fileType_);
        objectOutputStream.writeObject(caption_);
        objectOutputStream.writeInt(fileInfo_.getId());
    }

    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (Data.chatClientGui_ == null) {
            throw new IllegalStateException("Reading FileMessage as client is not allowed");
        } else if (Data.serverTempDir_ == null) {
            throw new IllegalStateException("Can't read FileMessage if no temp dir is not initialized");
        }
        final Object fileInfoMessasgeObject = objectInputStream.readObject();
        final FileInfoMessage fileInfoMessage = (FileInfoMessage) fileInfoMessasgeObject;
        long timesToRead = fileInfoMessage.getFileSize() / Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH;
        if (fileInfoMessage.getFileSize() % Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH != 0) {
            timesToRead += 1;
        }
        final File fileToWrite = new File(Data.serverTempDir_.toString() + File.separator + fileInfoMessage.getId());
        if (!fileToWrite.createNewFile()) {
            return;
        }
        final FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite, true);
        for (int i = 0; i < timesToRead; i++) {
            final Object filePartObject = objectInputStream.readObject();
            final FilePartMessage filePart = (FilePartMessage) filePartObject;
            final byte[] filePartBytes = filePart.getFilePartBytes();
            if (i == timesToRead - 1) {
                //filePartBytes = Arrays.copyOf(filePartBytes, (int) (Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH - (fileInfoMessage.getFileSize() - Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH * (timesToRead - 1))));
                //final int timesToWrite = (int) (Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH - (fileInfoMessage.getFileSize() - Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH * (timesToRead - 1)));
                // 10 - (15 - 10 * (2-1))
                // 1048576 - (19 - 1048576 * (1 - 1))
                // Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH - (fileInfoMessage.getFileSize() - Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH * (timesToRead - 1))
                final int timesToWrite = (int) (fileInfoMessage.getFileSize() - Constants.DEFAULT_BYTE_ARRAY_BUFFER_LENGTH * (timesToRead - 1));
                for (int x = 0; x < timesToWrite; x++) {
                    fileOutputStream.write(filePartBytes[x]);
                }
            } else {
                fileOutputStream.write(filePartBytes);
            }
        }
        fileOutputStream.close();
        sender_ = (UserInfo) objectInputStream.readObject();
        file_ = (File) objectInputStream.readObject();
        fileType_ = (FileType) objectInputStream.readObject();
        caption_ = (String) objectInputStream.readObject();
        fileInfo_ = new FileInfoMessage(getSender(), file_.getName(), fileType_, file_.length(), caption_, objectInputStream.readInt());
    }
}
