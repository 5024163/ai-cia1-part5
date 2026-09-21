package astar.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/** Page title at the top of the window. */
public class HeaderPanel extends JPanel {
    public HeaderPanel(int contentWidth) {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE));

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));
        inner.setPreferredSize(new Dimension(contentWidth, 112));

        JLabel title = new JLabel("AI-Based A* Path Planning");
        title.setFont(Theme.font(Font.BOLD, 34f));
        title.setForeground(Theme.INK);

        JLabel subtitle = new JLabel("Mobile Robot Navigation Simulator");
        subtitle.setFont(Theme.font(Font.BOLD, 18f));
        subtitle.setForeground(Theme.INK_SOFT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new javax.swing.BoxLayout(text, javax.swing.BoxLayout.Y_AXIS));
        text.add(title);
        text.add(javax.swing.Box.createVerticalStrut(4));
        text.add(subtitle);

        inner.add(text, BorderLayout.CENTER);
        inner.add(new RouteMark(), BorderLayout.EAST);
        add(inner);
    }

    /** Decorative stepped route: start dot, staircase path, goal dot. */
    private static class RouteMark extends JComponent {
        RouteMark() {
            setPreferredSize(new Dimension(240, 72));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(0, 0);
            g2.setColor(Theme.LINE);
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 12; i++) {
                g2.drawLine(i * 20, 0, i * 20, 72);
            }
            for (int i = 0; i <= 3; i++) {
                g2.drawLine(0, i * 24, 240, i * 24);
            }
            g2.setColor(Theme.OBSTACLE);
            g2.fillRect(80, 0, 20, 48);
            g2.fillRect(140, 24, 20, 48);

            Path2D route = new Path2D.Double();
            route.moveTo(10, 60);
            route.lineTo(110, 60);
            route.lineTo(110, 36);
            route.lineTo(130, 36);
            route.lineTo(130, 12);
            route.lineTo(210, 12);
            g2.setColor(Theme.PATH);
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(route);

            g2.setColor(Theme.ROBOT);
            g2.fill(new Ellipse2D.Double(3, 53, 14, 14));
            g2.setColor(Theme.GOAL);
            g2.fill(new Ellipse2D.Double(203, 5, 14, 14));
            g2.dispose();
        }
    }
}
