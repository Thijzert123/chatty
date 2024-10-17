package thijzert.chatty.client.gui;

/**
 * Launcher for <code>ChatClientGui</code> class. This is to avoid a bug in JavaFX 11.
 *
 * @author Thijzert
 * @see ChatClientGui
 */
public final class ChatClientGuiLauncher {
    /**
     * Main method of <code>ChatClientGuiLauncher</code>. It immediately calls <code>ChatClientGui.main(args)</code>.
     *
     * @param args args given on the commandline
     * @see ChatClientGui#main(String[])
     */
    public static void main(final String[] args) {
        ChatClientGui.main(args);
    }
}
