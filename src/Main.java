import javax.swing.*;

/**
 * Main
 * ----
 * Entry point of the FreelanceHub application.
 * Sets the system look-and-feel and launches the GUI on the
 * Swing Event-Dispatch Thread (the correct, thread-safe way).
 */
public class Main {
    public static void main(String[] args) {
        // use the OS look-and-feel for a nicer appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back silently to the default look-and-feel
        }

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
