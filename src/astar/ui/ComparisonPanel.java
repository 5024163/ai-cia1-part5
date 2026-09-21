package astar.ui;

import astar.algorithm.AlgorithmRunner;
import astar.controller.SimulationController;
import astar.model.AlgorithmMode;
import astar.model.Metrics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;
import java.util.function.Function;

/** The "Standard A* vs Optimized A*" card with the comparison table. */
public class ComparisonPanel extends CardPanel {
    /** One row of the table. */
    private record Row(String label, Function<Metrics, String> value, Function<Metrics, Double> better, boolean higherIsBetter) {
    }

    private final SimulationController controller;
    private final int inner;
    private final JPanel table = new JPanel(new GridBagLayout());
    private final InfoBox interpretation;
    private final FlatButton compareButton = new FlatButton("Run both on this map", FlatButton.Variant.SECONDARY);

    public ComparisonPanel(SimulationController controller, int width) {
        super(new BorderLayout(0, 14), width, 20, 20);
        this.controller = controller;
        this.inner = width - 40;
        setName("comparison");

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(Ui.label("Standard A* vs Optimized A*", Font.BOLD, 20f, Theme.INK), BorderLayout.WEST);
        compareButton.setFont(Theme.font(Font.BOLD, 13f));
        compareButton.addActionListener(e -> controller.compareBoth());
        titleRow.add(compareButton, BorderLayout.EAST);

        table.setOpaque(false);
        interpretation = new InfoBox(inner);

        JPanel middle = Ui.column();
        middle.add(Ui.left(table));
        Ui.gap(middle, 14);
        middle.add(Ui.left(interpretation));

        JPanel notes = Ui.column();
        notes.add(Ui.left(Ui.wrapped(inner,
                "\u2022  Execution time is the average of " + AlgorithmRunner.TIMING_RUNS
                        + " runs of the search alone (not the animation). On small grids both finish in a fraction "
                        + "of a millisecond, so small time differences can be measurement noise.",
                Font.PLAIN, 12f, Theme.MUTED)));
        Ui.gap(notes, 4);
        notes.add(Ui.left(Ui.wrapped(inner,
                "\u2022  Optimized A* is not guaranteed to be faster, or to expand fewer nodes, on every map. "
                        + "Results depend on where the obstacles are.", Font.PLAIN, 12f, Theme.MUTED)));
        Ui.gap(notes, 4);
        notes.add(Ui.left(Ui.wrapped(inner,
                "\u2022  Green cells mark the better value for that metric. Results reset when the map changes.",
                Font.PLAIN, 12f, Theme.MUTED)));

        add(titleRow, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(notes, BorderLayout.SOUTH);
        refresh();
    }

    /** Called whenever the controller's state changed. */
    public void refresh() {
        Map<AlgorithmMode, Metrics> results = controller.comparison();
        Metrics standard = results.get(AlgorithmMode.STANDARD);
        Metrics optimized = results.get(AlgorithmMode.OPTIMIZED);
        boolean bothFound = standard != null && optimized != null && standard.found() && optimized.found();

        compareButton.setEnabled(!controller.isRunning());

        table.removeAll();
        addCell(0, 0, "Metric", Font.BOLD, Theme.INK_SOFT, SwingConstants.LEFT, null);
        addCell(1, 0, "Standard A*", Font.BOLD, Theme.INK_SOFT, SwingConstants.RIGHT, null);
        addCell(2, 0, "Optimized A*", Font.BOLD, Theme.INK_SOFT, SwingConstants.RIGHT, null);

        Row[] rows = {
                new Row("Path length", m -> m.found() ? m.pathLength() + " moves" : "-", null, false),
                new Row("Nodes expanded", m -> String.valueOf(m.nodesExpanded()), m -> (double) m.nodesExpanded(), false),
                new Row("Execution time", m -> Format.time(m.executionTimeMs()), null, false),
                new Row("Search efficiency", m -> m.found() ? Format.percent(m.efficiency()) : "-", Metrics::efficiency, true),
        };
        for (int i = 0; i < rows.length; i++) {
            Row row = rows[i];
            addCell(0, i + 1, row.label(), Font.PLAIN, Theme.INK_SOFT, SwingConstants.LEFT, null);
            boolean standardWins = false;
            boolean optimizedWins = false;
            if (bothFound && row.better() != null) {
                double s = row.better().apply(standard);
                double o = row.better().apply(optimized);
                if (Math.abs(s - o) >= 0.05) {
                    boolean standardBetter = row.higherIsBetter() ? s > o : s < o;
                    standardWins = standardBetter;
                    optimizedWins = !standardBetter;
                }
            }
            addValue(1, i + 1, standard, row, standardWins);
            addValue(2, i + 1, optimized, row, optimizedWins);
        }

        interpretation.set(interpret(standard, optimized));
        table.revalidate();
        table.repaint();
    }

    private void addValue(int col, int row, Metrics m, Row r, boolean winner) {
        if (m == null) {
            addCell(col, row, "not run yet", Font.PLAIN, Theme.MUTED, SwingConstants.RIGHT, null);
        } else if (winner) {
            addCell(col, row, r.value().apply(m), Font.BOLD, Theme.GOAL, SwingConstants.RIGHT, Theme.tint(Theme.GOAL, 0.10));
        } else {
            addCell(col, row, r.value().apply(m), Font.BOLD, Theme.INK, SwingConstants.RIGHT, null);
        }
    }

    private void addCell(int col, int row, String text, int style, Color color, int align, Color background) {
        JLabel label = new JLabel(text, align);
        label.setFont(Theme.font(style, 14f));
        label.setForeground(color);
        label.setOpaque(background != null);
        if (background != null) {
            label.setBackground(background);
        }
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, row < 4 ? 1 : 0, 0, Theme.LINE),
                BorderFactory.createEmptyBorder(11, 14, 11, 14)));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = col;
        c.gridy = row;
        c.weightx = col == 0 ? 0.4 : 0.3;
        c.fill = GridBagConstraints.BOTH;
        table.add(label, c);
    }

    /** Plain-language reading of the two results. Never claims more than the numbers show. */
    private static String interpret(Metrics standard, Metrics optimized) {
        if (standard == null || optimized == null) {
            return "Run both algorithms on the same map to compare them. \"Run both on this map\" does it instantly.";
        }
        if (!standard.found() && !optimized.found()) {
            return "Neither algorithm found a path, so the goal cannot be reached on this map.";
        }
        if (standard.pathLength() != optimized.pathLength()) {
            return "The path lengths differ. With an admissible heuristic on this grid both should be equal, so check the map.";
        }
        int diff = standard.nodesExpanded() - optimized.nodesExpanded();
        int n = standard.pathLength();
        if (diff > 0) {
            long pct = Math.round(diff * 100.0 / standard.nodesExpanded());
            return "Both found a path of " + n + " moves, so path quality is the same. Optimized A* expanded "
                    + diff + " fewer nodes (" + pct + "% fewer), which indicates less unnecessary exploration on this map.";
        }
        if (diff == 0) {
            return "Both found a path of " + n + " moves and expanded the same number of nodes, "
                    + "so the optimization made no difference on this map.";
        }
        return "Both found a path of " + n + " moves, but Optimized A* expanded " + (-diff)
                + " more nodes here. Tie-breaking is only a heuristic, so fewer expansions are not guaranteed on every map.";
    }

    /** A grey rounded box holding one paragraph. */
    private static class InfoBox extends JPanel {
        private final WrapText text;

        InfoBox(int width) {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            text = Ui.wrapped(width - 28, " ", Font.PLAIN, 14f, Theme.INK);
            add(text, BorderLayout.CENTER);
        }

        void set(String message) {
            text.setText(message);
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
