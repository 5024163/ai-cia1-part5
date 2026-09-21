package astar.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** A row of mutually exclusive buttons (like radio buttons). */
public class SegmentedControl<T> extends JPanel {
    public record Option<T>(T value, String label, Icons.Kind icon) {
        public Option(T value, String label) {
            this(value, label, null);
        }
    }

    private final List<Pill> pills = new ArrayList<>();
    private final Consumer<T> onChange;
    private T value;

    public SegmentedControl(List<Option<T>> options, T initial, Consumer<T> onChange) {
        super(new GridLayout(1, options.size(), 4, 0));
        this.onChange = onChange;
        this.value = initial;
        setOpaque(false);
        setBorder(new EmptyBorder(4, 4, 4, 4));
        for (Option<T> option : options) {
            Pill pill = new Pill(option);
            pills.add(pill);
            add(pill);
        }
    }

    public void setValue(T newValue) {
        this.value = newValue;
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Pill p : pills) {
            p.setEnabled(enabled);
            p.setCursor(Cursor.getPredefinedCursor(enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.FLOOR);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }

    private void choose(Option<T> option) {
        if (isEnabled()) {
            onChange.accept(option.value());
        }
    }

    /** One button inside the control. */
    private class Pill extends JComponent {
        private final Option<T> option;
        private boolean hover;

        Pill(Option<T> option) {
            this.option = option;
            setFont(Theme.font(java.awt.Font.BOLD, 14f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    choose(option);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int w = fm.stringWidth(option.label()) + 28 + (option.icon() != null ? 24 : 0);
            return new Dimension(w, 36);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean selected = option.value().equals(value);
            boolean enabled = isEnabled();
            int w = getWidth();
            int h = getHeight();

            if (selected) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g2.setColor(Theme.LINE);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            }

            Color textColor = selected ? Theme.INK : (hover && enabled ? Theme.INK : Theme.MUTED);
            if (!enabled) {
                textColor = new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 120);
            }
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(option.label());
            int iconW = option.icon() != null ? 22 : 0;
            int x = (w - (iconW + textW)) / 2;
            if (option.icon() != null) {
                Icons.paint(g2, option.icon(), x, (h - 18) / 2.0, 18, textColor);
            }
            g2.setColor(textColor);
            g2.drawString(option.label(), x + iconW, (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
