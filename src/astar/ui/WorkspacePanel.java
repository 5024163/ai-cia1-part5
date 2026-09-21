package astar.ui;

import astar.controller.SimulationController;
import astar.model.Tool;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/** The "Robot workspace" card: tool picker, the grid and the colour legend. */
public class WorkspacePanel extends CardPanel {
    private final SimulationController controller;
    private final GridPanel gridPanel;
    private final JLabel sizeLabel = new JLabel();
    private final SegmentedControl<Tool> tools;

    public WorkspacePanel(SimulationController controller, int width) {
        super(new BorderLayout(0, 14), width, 24, 20);
        this.controller = controller;
        setName("workspace");

        // ----- header row: title on the left, tool picker on the right -----
        JLabel title = new JLabel("Robot workspace");
        title.setFont(Theme.font(Font.BOLD, 20f));
        title.setForeground(Theme.INK);
        sizeLabel.setFont(Theme.font(Font.PLAIN, 14f));
        sizeLabel.setForeground(Theme.MUTED);

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(sizeLabel);

        tools = new SegmentedControl<>(List.of(
                new SegmentedControl.Option<>(Tool.START, "Set Start", Icons.Kind.ROBOT),
                new SegmentedControl.Option<>(Tool.GOAL, "Set Goal", Icons.Kind.FLAG),
                new SegmentedControl.Option<>(Tool.OBSTACLE, "Add Obstacles", Icons.Kind.WALL)),
                controller.tool(), controller::setTool);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleBox, BorderLayout.WEST);
        header.add(tools, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ----- grid -----
        gridPanel = new GridPanel(controller);
        JPanel gridHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        gridHolder.setOpaque(false);
        gridHolder.add(gridPanel);
        add(gridHolder, BorderLayout.CENTER);

        add(buildLegend(), BorderLayout.SOUTH);
        refresh();
    }

    public GridPanel gridPanel() {
        return gridPanel;
    }

    /** Called whenever the controller's state changed. */
    public void refresh() {
        sizeLabel.setText(controller.cols() + " x " + controller.rows());
        tools.setValue(controller.tool());
        tools.setEnabled(!controller.isRunning());
        gridPanel.syncSize();
        gridPanel.repaint();
    }

    // ---------- legend ----------

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new java.awt.GridLayout(2, 4, 12, 8));
        legend.setOpaque(false);
        legend.add(legendItem("Free cell", Theme.CELL, true));
        legend.add(legendItem("Obstacle", Theme.OBSTACLE, false));
        legend.add(legendItem("Robot / start", Theme.ROBOT, false));
        legend.add(legendItem("Goal", Theme.GOAL, false));
        legend.add(legendItem("Open set (waiting)", Theme.FRONTIER, false));
        legend.add(legendItem("Explored", Theme.VISITED, false));
        legend.add(legendItem("Current node", Theme.EXPANDING, false));
        legend.add(legendItem("Final path", Theme.PATH, false));
        return legend;
    }

    private static JComponent legendItem(String text, Color color, boolean outlined) {
        JPanel item = new JPanel();
        item.setOpaque(false);
        item.setLayout(new BoxLayout(item, BoxLayout.X_AXIS));
        item.add(new Swatch(color, outlined));
        item.add(Box.createHorizontalStrut(7));
        JLabel label = new JLabel(text);
        label.setFont(Theme.font(Font.PLAIN, 13f));
        label.setForeground(Theme.INK_SOFT);
        item.add(label);
        return item;
    }

    private static class Swatch extends JComponent {
        private final Color color;
        private final boolean outlined;

        Swatch(Color color, boolean outlined) {
            this.color = color;
            this.outlined = outlined;
            setPreferredSize(new Dimension(16, 16));
            setMaximumSize(new Dimension(16, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, 15, 15, 4, 4);
            if (outlined) {
                g2.setColor(Theme.LINE);
                g2.drawRoundRect(0, 0, 15, 15, 4, 4);
            }
            g2.dispose();
        }
    }
}
