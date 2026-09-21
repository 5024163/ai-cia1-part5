package astar.ui;

import astar.controller.SimulationController;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

/** The main window. It builds the page and redraws it when the controller reports a change. */
public class MainFrame extends JFrame implements SimulationController.Listener {
    /** Width of the page content in pixels. */
    private static final int CONTENT_WIDTH = 1240;
    private static final int GAP = 24;
    private static final int LEFT_WIDTH = 840;
    private static final int RIGHT_WIDTH = CONTENT_WIDTH - LEFT_WIDTH - GAP;
    private static final int HALF_WIDTH = (CONTENT_WIDTH - GAP) / 2;

    private final SimulationController controller = new SimulationController();
    private final WorkspacePanel workspace;
    private final ControlsPanel controls;
    private final ResultsPanel results;
    private final ComparisonPanel comparison;

    public MainFrame() {
        super("A* Path Planning for Mobile Robots");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        workspace = new WorkspacePanel(controller, LEFT_WIDTH);
        results = new ResultsPanel(controller, LEFT_WIDTH);
        controls = new ControlsPanel(controller, RIGHT_WIDTH);
        InstructionsPanel instructions = new InstructionsPanel(RIGHT_WIDTH);
        comparison = new ComparisonPanel(controller, HALF_WIDTH);
        ExplanationPanel explanation = new ExplanationPanel(HALF_WIDTH);
        RealWorldPanel realWorld = new RealWorldPanel(CONTENT_WIDTH);

        // ----- top row: [workspace + results] | [controls + instructions] -----
        JPanel leftColumn = column(workspace, results);
        JPanel rightColumn = column(controls, instructions);
        JPanel topRow = row(leftColumn, rightColumn);

        // ----- second row: comparison | explanation -----
        JPanel secondRow = row(comparison, explanation);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(topRow);
        content.add(javax.swing.Box.createVerticalStrut(GAP));
        content.add(secondRow);
        content.add(javax.swing.Box.createVerticalStrut(GAP));
        content.add(realWorld);

        JPanel centered = new JPanel(new GridBagLayout());
        centered.setOpaque(false);
        centered.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, 0));
        centered.add(content);

        Page page = new Page();
        page.add(fullWidth(new HeaderPanel(CONTENT_WIDTH)));
        page.add(fullWidth(centered));
        page.add(fullWidth(new FooterPanel(CONTENT_WIDTH)));

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        setContentPane(scroll);

        controller.setListener(this);

        setMinimumSize(new Dimension(700, 500));
        setSize(1320, 900);
        setLocationRelativeTo(null);
    }

    // ---------- Listener ----------

    @Override
    public void onVisualChange() {
        workspace.gridPanel().repaint();
    }

    @Override
    public void onStateChange() {
        workspace.refresh();
        controls.refresh();
        results.refresh();
        comparison.refresh();
        revalidate();
        repaint();
    }

    // ---------- layout helpers ----------

    private static JPanel column(java.awt.Component top, java.awt.Component bottom) {
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.add(top);
        column.add(javax.swing.Box.createVerticalStrut(GAP));
        column.add(bottom);
        return column;
    }

    private static JPanel row(java.awt.Component left, java.awt.Component right) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, GAP);
        row.add(left, c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        row.add(right, c);
        return row;
    }

    private static JPanel fullWidth(JPanel panel) {
        panel.setAlignmentX(0f);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    /** The scrolling page. It stretches to the window width unless the window is narrower than the content. */
    private static class Page extends JPanel implements Scrollable {
        Page() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Theme.FLOOR);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(100, visibleRect.height - 60);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getParent() != null && getParent().getWidth() >= getPreferredSize().width;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
