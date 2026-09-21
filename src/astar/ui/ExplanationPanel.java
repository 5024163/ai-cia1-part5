package astar.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/** The "How A* works" card: formula, steps, why it suits robots, what this project implements. */
public class ExplanationPanel extends CardPanel {
    public ExplanationPanel(int width) {
        super(new BorderLayout(), width, 20, 20);
        setName("explanation");
        int inner = width - 40;

        JPanel col = Ui.column();
        add(col, BorderLayout.CENTER);

        col.add(Ui.left(Ui.label("How A* works", Font.BOLD, 20f, Theme.INK)));
        Ui.gap(col, 8);
        col.add(Ui.left(Ui.wrapped(inner,
                "A* combines what the robot already knows (the distance it has travelled) with an educated guess "
                        + "(the distance it still has to go). That is why it explores far less than a blind search.",
                Font.PLAIN, 15f, Theme.INK_SOFT)));
        Ui.gap(col, 14);

        // ----- formula box -----
        DarkBox formula = new DarkBox();
        formula.add(Ui.left(Ui.label("f(n) = g(n) + h(n)", Font.BOLD, 28f, Color.WHITE)));
        Ui.gap(formula, 10);
        JPanel three = new JPanel(new GridLayout(1, 3, 14, 0));
        three.setOpaque(false);
        three.add(term("g(n): actual cost", "Moves made from the start to cell n.", new Color(0x9db8f5), (inner - 60) / 3));
        three.add(term("h(n): estimated cost", "Manhattan distance to the goal: |x1 - x2| + |y1 - y2|.", new Color(0xf8c45a), (inner - 60) / 3));
        three.add(term("f(n): estimated total", "Expected cost of a route that passes through n.", new Color(0x8de0bd), (inner - 60) / 3));
        formula.add(Ui.left(three));
        col.add(Ui.left(formula));
        Ui.gap(col, 14);

        // ----- quote -----
        WrapText quote = Ui.wrapped(inner - 20,
                "The algorithm selects the node with the lowest estimated total cost and continues exploring "
                        + "until the goal is reached.", Font.PLAIN, 15f, Theme.INK);
        quote.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.PATH),
                BorderFactory.createEmptyBorder(2, 12, 2, 0)));
        col.add(Ui.left(quote));
        Ui.gap(col, 18);

        // ----- steps -----
        col.add(Ui.left(Ui.label("The steps", Font.BOLD, 17f, Theme.INK)));
        Ui.gap(col, 6);
        String[] steps = {
                "Put the start cell in the open set with g = 0.",
                "Take the cell with the lowest f(n) out of the open set.",
                "If it is the goal, follow the parent links back to the start. That is the path.",
                "Otherwise look at its four neighbours (up, down, left, right) and skip obstacles.",
                "For each neighbour, calculate g, h and f. If a cheaper route to it is found, update its cost and parent.",
                "Repeat. If the open set becomes empty, no path exists."
        };
        for (int i = 0; i < steps.length; i++) {
            col.add(Ui.left(Ui.wrapped(inner, (i + 1) + ".  " + steps[i], Font.PLAIN, 14f, Theme.INK_SOFT)));
            Ui.gap(col, 4);
        }
        Ui.gap(col, 14);

        // ----- why A* suits robots -----
        col.add(Ui.left(Ui.label("Why A* suits mobile robots", Font.BOLD, 17f, Theme.INK)));
        Ui.gap(col, 8);
        JPanel reasons = new JPanel(new GridLayout(2, 2, 16, 12));
        reasons.setOpaque(false);
        int half = (inner - 16) / 2;
        reasons.add(item("Finds efficient routes", "With an admissible heuristic, A* returns a shortest path.", half));
        reasons.add(item("Considers obstacles", "Blocked cells are never expanded, so the route always avoids them.", half));
        reasons.add(item("Uses actual and estimated cost", "g(n) tracks what was travelled; h(n) points the search at the goal.", half));
        reasons.add(item("Fits grid and graph maps", "Robot floor plans are often stored as grids or graphs, which is exactly what A* searches.", half));
        col.add(Ui.left(reasons));
        Ui.gap(col, 18);

        // ----- what this project implements -----
        col.add(Ui.left(Ui.label("What this project implements", Font.BOLD, 17f, Theme.INK)));
        Ui.gap(col, 8);
        col.add(Ui.left(item("Standard A*",
                "The textbook algorithm with a list as the open list. Uses Manhattan distance and 4-directional movement.", inner)));
        Ui.gap(col, 8);
        col.add(Ui.left(item("Optimized A* (educational optimization)",
                "A simple, defensible speed-up: binary-heap priority queue, tie-breaking towards the goal, skipping "
                        + "duplicate entries and stopping as soon as the goal is reached. It keeps the path optimal.", inner)));
        Ui.gap(col, 8);
        col.add(Ui.left(item("Relation to the research paper",
                "This project implements an educational simulation based on the research problem of optimizing A* for "
                        + "mobile robotic navigation. It does not claim to reproduce the paper's exact method.", inner)));
    }

    private static JPanel term(String title, String text, Color titleColor, int width) {
        JPanel p = Ui.column();
        p.add(Ui.left(Ui.wrapped(width, title, Font.BOLD, 13f, titleColor)));
        Ui.gap(p, 3);
        p.add(Ui.left(Ui.wrapped(width, text, Font.PLAIN, 13f, new Color(255, 255, 255, 205))));
        return p;
    }

    private static JPanel item(String title, String text, int width) {
        JPanel p = Ui.column();
        p.add(Ui.left(Ui.wrapped(width, title, Font.BOLD, 14f, Theme.INK)));
        Ui.gap(p, 2);
        p.add(Ui.left(Ui.wrapped(width, text, Font.PLAIN, 13f, Theme.MUTED)));
        return p;
    }

    /** Dark rounded box used for the formula. */
    private static class DarkBox extends JPanel {
        DarkBox() {
            setOpaque(false);
            setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.INK);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
