package astar;

import astar.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Starts the A* path planning simulator. */
public class Main {
    public static void main(String[] args) {
        // Smoother text on all platforms.
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // The default look and feel is fine too.
            }
            new MainFrame().setVisible(true);
        });
    }
}
