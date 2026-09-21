package astar.ui;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

/** Small helpers so the panel classes stay short and readable. */
final class Ui {
    private Ui() {
    }

    static JLabel label(String text, int style, float size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.font(style, size));
        label.setForeground(color);
        return label;
    }

    /** A paragraph whose text wraps at the given pixel width. */
    static WrapText wrapped(int width, String text, int style, float size, Color color) {
        return new WrapText(width, text, style, size, color);
    }

    /** A transparent panel that stacks its children from top to bottom. */
    static JPanel column() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    /** Aligns a component to the left inside a column. */
    static <T extends JComponent> T left(T component) {
        component.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return component;
    }

    static void gap(JPanel column, int height) {
        column.add(javax.swing.Box.createVerticalStrut(height));
    }

}
