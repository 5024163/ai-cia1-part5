package astar.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/** The "Real-World Application" card. */
public class RealWorldPanel extends CardPanel {
    public RealWorldPanel(int width) {
        super(new BorderLayout(0, 14), width, 20, 24);
        setName("realworld");
        int inner = width - 48;

        add(Ui.label("Real-World Application", Font.BOLD, 20f, Theme.INK), BorderLayout.NORTH);

        // ----- example box on the left -----
        int leftWidth = 400;
        ExampleBox example = new ExampleBox();
        example.add(Ui.left(Ui.label("Example", Font.BOLD, 15f, Theme.INK)));
        Ui.gap(example, 4);
        example.add(Ui.left(Ui.wrapped(leftWidth - 40,
                "A warehouse robot needs to move from a storage area to a delivery area while avoiding blocked aisles.",
                Font.PLAIN, 15f, Theme.INK_SOFT)));
        Ui.gap(example, 10);
        example.add(Ui.left(Ui.wrapped(leftWidth - 40,
                "In this simulator the grid is the warehouse floor, obstacles are blocked aisles or shelving, and the "
                        + "robot icon is the delivery robot. The same idea works on a real robot: turn its map into a "
                        + "grid or graph, then run A* from where it is to where it must go.",
                Font.PLAIN, 13f, Theme.MUTED)));

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(example, BorderLayout.NORTH);

        // ----- applications on the right -----
        int colWidth = (inner - leftWidth - 24 - 24) / 2;
        JPanel apps = new JPanel(new GridLayout(3, 2, 24, 16));
        apps.setOpaque(false);
        apps.add(app("Warehouse robots", "Carry stock between shelves and packing stations while avoiding pallets and other robots.", colWidth));
        apps.add(app("Hospital delivery robots", "Move medicine and samples through corridors that are often blocked by beds and people.", colWidth));
        apps.add(app("Factory robots", "Transport parts between machines on floors where equipment layouts change.", colWidth));
        apps.add(app("Indoor autonomous vehicles", "Plan routes through car parks, airports and campuses using a stored map.", colWidth));
        apps.add(app("Hazardous-environment robots", "Reach a target in damaged or unsafe areas without a human entering, routing around blocked zones.", colWidth));
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(apps, BorderLayout.NORTH);

        JPanel row = new JPanel(new BorderLayout(24, 0));
        row.setOpaque(false);
        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.CENTER);
        add(row, BorderLayout.CENTER);
    }

    private static JPanel app(String title, String text, int width) {
        JPanel p = Ui.column();
        p.add(Ui.left(Ui.wrapped(width, title, Font.BOLD, 15f, Theme.INK)));
        Ui.gap(p, 2);
        p.add(Ui.left(Ui.wrapped(width, text, Font.PLAIN, 13f, Theme.MUTED)));
        return p;
    }

    private static class ExampleBox extends JPanel {
        ExampleBox() {
            setOpaque(false);
            setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.FLOOR);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
