package astar.ui;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * A read-only paragraph that wraps its text at a fixed pixel width.
 * (A JLabel cannot wrap text by itself, and HTML widths in Swing are not reliable.)
 */
class WrapText extends JTextArea {
    private final int width;

    WrapText(int width, String text, int style, float size, Color color) {
        super(text);
        this.width = width;
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setBorder(null);
        setFont(Theme.font(style, size));
        setForeground(color);
        // Without this, changing the text would make the whole page scroll to this paragraph.
        setCaret(new NoScrollCaret());
    }

    /** A caret that never asks the scroll pane to scroll. */
    private static class NoScrollCaret extends DefaultCaret {
        @Override
        protected void adjustVisibility(Rectangle nloc) {
            // do nothing
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // The text must know its width before it can work out how many lines it needs.
        setSize(width, Short.MAX_VALUE);
        Dimension d = super.getPreferredSize();
        return new Dimension(width, d.height);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
