package astar.ui;

import astar.controller.SimulationController;
import astar.model.Metrics;
import astar.model.Status;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/** The "Results" card: status message and the four measured values. */
public class ResultsPanel extends CardPanel {
    private final SimulationController controller;
    private final JLabel modeLabel = Ui.label("", Font.BOLD, 14f, Theme.MUTED);
    private final Banner banner;
    private final Tile pathLength;
    private final Tile nodesExpanded;
    private final Tile executionTime;
    private final Tile efficiency;

    public ResultsPanel(SimulationController controller, int width) {
        super(new BorderLayout(0, 12), width, 20, 20);
        this.controller = controller;
        setName("results");
        int inner = width - 40;

        // title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(Ui.label("Results", Font.BOLD, 20f, Theme.INK), BorderLayout.WEST);
        titleRow.add(modeLabel, BorderLayout.EAST);

        banner = new Banner(inner);

        JPanel tiles = new JPanel(new GridLayout(1, 4, 10, 0));
        tiles.setOpaque(false);
        pathLength = new Tile("Path length", "moves from start to goal");
        nodesExpanded = new Tile("Nodes expanded", "cells the search examined");
        executionTime = new Tile("Execution time", "average of 20 runs, search only");
        efficiency = new Tile("Search efficiency", "path length / nodes expanded");
        tiles.add(pathLength);
        tiles.add(nodesExpanded);
        tiles.add(executionTime);
        tiles.add(efficiency);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(titleRow, BorderLayout.NORTH);
        top.add(banner, BorderLayout.CENTER);
        top.add(tiles, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        add(Ui.wrapped(inner,
                "Search efficiency shows how much of the exploration ended up on the final route "
                        + "(100% = no cell was expanded unnecessarily). All values are measured from the run "
                        + "you just watched.", Font.PLAIN, 12f, Theme.MUTED), BorderLayout.SOUTH);
        refresh();
    }

    /** Called whenever the controller's state changed. */
    public void refresh() {
        Metrics m = controller.latest();
        modeLabel.setText(m != null ? m.mode().label() : "");
        banner.set(controller.status());

        boolean hasPath = m != null && m.found();
        pathLength.setValue(hasPath ? String.valueOf(m.pathLength()) : "-");
        nodesExpanded.setValue(m != null ? String.valueOf(m.nodesExpanded()) : "-");
        executionTime.setValue(m != null ? Format.time(m.executionTimeMs()) : "-");
        efficiency.setValue(hasPath ? Format.percent(m.efficiency()) : "-");
    }

    // ---------- status banner ----------

    private static class Banner extends JPanel {
        private final WrapText text;
        private Color fill = Theme.FLOOR;
        private Color accent = Theme.INK_SOFT;

        Banner(int width) {
            super(new BorderLayout());
            setOpaque(false);
            setName("status");
            setBorder(new EmptyBorder(11, 38, 11, 14));
            text = Ui.wrapped(width - 56, " ", Font.BOLD, 14f, Theme.INK_SOFT);
            add(text, BorderLayout.CENTER);
        }

        void set(Status status) {
            switch (status.kind()) {
                case RUNNING -> accent = Theme.ROBOT;
                case SUCCESS -> accent = Theme.GOAL;
                case ERROR -> accent = Theme.EXPANDING;
                default -> accent = Theme.INK_SOFT;
            }
            fill = switch (status.kind()) {
                case RUNNING, SUCCESS, ERROR -> Theme.tint(accent, 0.10);
                default -> Theme.FLOOR;
            };
            text.setForeground(accent);
            text.setText(status.message());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(accent);
            g2.fillOval(14, getHeight() / 2 - 6, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------- one metric tile ----------

    private static class Tile extends JPanel {
        private final JLabel value = Ui.label("-", Font.BOLD, 28f, Theme.INK);

        Tile(String name, String hint) {
            setOpaque(false);
            setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(12, 16, 12, 12));
            add(Ui.left(Ui.label(name, Font.BOLD, 14f, Theme.INK_SOFT)));
            add(javax.swing.Box.createVerticalStrut(2));
            add(Ui.left(value));
            add(javax.swing.Box.createVerticalStrut(2));
            add(Ui.left(Ui.wrapped(158, hint, Font.PLAIN, 12f, Theme.MUTED)));
        }

        void setValue(String text) {
            value.setText(text);
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.LINE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
