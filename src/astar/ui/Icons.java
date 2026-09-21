package astar.ui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** Small vector icons (drawn with Java2D, so no image files are needed). */
public final class Icons {
    public enum Kind { ROBOT, FLAG, DOCK, WALL }

    private Icons() {
    }

    /** Draws an icon inside the square (x, y, s, s). */
    public static void paint(Graphics2D g, Kind kind, double x, double y, double s, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke((float) (s * 0.09), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (kind) {
            case ROBOT -> {
                g.draw(new RoundRectangle2D.Double(x + 0.2 * s, y + 0.38 * s, 0.6 * s, 0.42 * s, 0.14 * s, 0.14 * s));
                g.draw(new Line2D.Double(x + 0.5 * s, y + 0.38 * s, x + 0.5 * s, y + 0.22 * s));
                g.fill(new Ellipse2D.Double(x + 0.44 * s, y + 0.13 * s, 0.12 * s, 0.12 * s));
                g.fill(new Ellipse2D.Double(x + 0.34 * s, y + 0.53 * s, 0.1 * s, 0.1 * s));
                g.fill(new Ellipse2D.Double(x + 0.56 * s, y + 0.53 * s, 0.1 * s, 0.1 * s));
                g.draw(new Line2D.Double(x + 0.11 * s, y + 0.52 * s, x + 0.11 * s, y + 0.66 * s));
                g.draw(new Line2D.Double(x + 0.89 * s, y + 0.52 * s, x + 0.89 * s, y + 0.66 * s));
            }
            case FLAG -> {
                g.draw(new Line2D.Double(x + 0.3 * s, y + 0.14 * s, x + 0.3 * s, y + 0.88 * s));
                Path2D flag = new Path2D.Double();
                flag.moveTo(x + 0.3 * s, y + 0.16 * s);
                flag.lineTo(x + 0.82 * s, y + 0.16 * s);
                flag.lineTo(x + 0.68 * s, y + 0.34 * s);
                flag.lineTo(x + 0.82 * s, y + 0.52 * s);
                flag.lineTo(x + 0.3 * s, y + 0.52 * s);
                flag.closePath();
                g.fill(flag);
            }
            case DOCK -> {
                g.draw(new Ellipse2D.Double(x + 0.2 * s, y + 0.2 * s, 0.6 * s, 0.6 * s));
                g.fill(new Ellipse2D.Double(x + 0.41 * s, y + 0.41 * s, 0.18 * s, 0.18 * s));
            }
            case WALL -> {
                g.draw(new RoundRectangle2D.Double(x + 0.14 * s, y + 0.14 * s, 0.72 * s, 0.72 * s, 0.1 * s, 0.1 * s));
                g.draw(new Line2D.Double(x + 0.14 * s, y + 0.38 * s, x + 0.86 * s, y + 0.38 * s));
                g.draw(new Line2D.Double(x + 0.14 * s, y + 0.62 * s, x + 0.86 * s, y + 0.62 * s));
                g.draw(new Line2D.Double(x + 0.5 * s, y + 0.14 * s, x + 0.5 * s, y + 0.38 * s));
                g.draw(new Line2D.Double(x + 0.32 * s, y + 0.38 * s, x + 0.32 * s, y + 0.62 * s));
                g.draw(new Line2D.Double(x + 0.68 * s, y + 0.38 * s, x + 0.68 * s, y + 0.62 * s));
                g.draw(new Line2D.Double(x + 0.5 * s, y + 0.62 * s, x + 0.5 * s, y + 0.86 * s));
            }
        }
    }

    /** Wraps an icon so it can be used in buttons and labels. */
    public static Icon icon(Kind kind, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                paint(g2, kind, x, y, size, color);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
}
