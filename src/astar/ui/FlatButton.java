package astar.ui;

import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** A flat, rounded button in three styles. */
public class FlatButton extends JButton {
    public enum Variant { PRIMARY, SECONDARY, DANGER }

    private Variant variant;
    private Icon buttonIcon;

    public FlatButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(Theme.font(java.awt.Font.BOLD, 14f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
        repaint();
    }

    public void setButtonIcon(Icon icon) {
        this.buttonIcon = icon;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        FontMetrics fm = getFontMetrics(getFont());
        int w = fm.stringWidth(getText()) + 28 + (buttonIcon != null ? buttonIcon.getIconWidth() + 8 : 0);
        return new Dimension(w, 42);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (!isEnabled()) {
            g2.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        }

        boolean hover = isEnabled() && getModel().isRollover();
        Color fill;
        Color border;
        Color text;
        switch (variant) {
            case PRIMARY -> {
                fill = hover ? Theme.ROBOT_DARK : Theme.ROBOT;
                border = fill;
                text = Color.WHITE;
            }
            case DANGER -> {
                fill = hover ? Theme.tint(Theme.EXPANDING, 0.12) : Color.WHITE;
                border = Theme.EXPANDING;
                text = Theme.EXPANDING;
            }
            default -> {
                fill = hover ? Theme.FLOOR : Color.WHITE;
                border = Theme.LINE;
                text = Theme.INK_SOFT;
            }
        }

        int w = getWidth();
        int h = getHeight();
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
        g2.setColor(border);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
        if (variant == Variant.DANGER) {
            g2.drawRoundRect(1, 1, w - 3, h - 3, 11, 11);
        }

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(getText());
        int iconW = buttonIcon != null ? buttonIcon.getIconWidth() + 8 : 0;
        int x = (w - (iconW + textW)) / 2;
        if (buttonIcon != null) {
            buttonIcon.paintIcon(this, g2, x, (h - buttonIcon.getIconHeight()) / 2);
        }
        g2.setColor(text);
        g2.drawString(getText(), x + iconW, (h - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
