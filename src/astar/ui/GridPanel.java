package astar.ui;

import astar.controller.SimulationController;
import astar.model.CellVisual;
import astar.model.Position;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Draws the grid and the robot, and sends mouse clicks / drags to the controller.
 * It does not contain any path-finding logic.
 */
public class GridPanel extends JPanel {
    /** Available width for the grid inside its card. */
    private static final int MAX_WIDTH = 800;

    private final SimulationController controller;
    private int cell = 32;

    public GridPanel(SimulationController controller) {
        this.controller = controller;
        setName("grid");
        setOpaque(true);
        setBackground(Theme.LINE);
        syncSize();

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Position pos = cellAt(e.getPoint());
                if (pos != null && e.getButton() == MouseEvent.BUTTON1) {
                    controller.cellPressed(pos);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Position pos = cellAt(e.getPoint());
                if (pos != null) {
                    controller.cellDragged(pos);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                controller.editEnded();
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    /** Recalculates the cell size after the grid size changed. */
    public void syncSize() {
        cell = Math.max(12, MAX_WIDTH / controller.cols());
        Dimension d = new Dimension(cell * controller.cols() + 1, cell * controller.rows() + 1);
        if (!d.equals(getPreferredSize())) {
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
            revalidate();
        }
        setCursor(Cursor.getPredefinedCursor(controller.isRunning() ? Cursor.DEFAULT_CURSOR : Cursor.CROSSHAIR_CURSOR));
    }

    private Position cellAt(Point p) {
        if (p.x < 0 || p.y < 0) {
            return null;
        }
        int col = p.x / cell;
        int row = p.y / cell;
        if (row < 0 || row >= controller.rows() || col < 0 || col >= controller.cols()) {
            return null;
        }
        return new Position(row, col);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean[][] obstacles = controller.obstacles();
        CellVisual[][] overlay = controller.overlay();
        Position start = controller.start();
        Position goal = controller.goal();
        Position robot = controller.robotPos();

        for (int r = 0; r < controller.rows(); r++) {
            for (int c = 0; c < controller.cols(); c++) {
                Position pos = new Position(r, c);
                boolean isStart = pos.equals(start);
                boolean isGoal = pos.equals(goal);
                // With no robot position yet, the robot sits on the start cell.
                boolean hasRobot = robot != null ? pos.equals(robot) : isStart;

                int x = c * cell;
                int y = r * cell;
                int size = cell - 1;

                if (obstacles[r][c]) {
                    paintObstacle(g2, x, y, size);
                    continue;
                }

                Color background;
                if (hasRobot) {
                    background = isGoal ? Theme.GOAL : Theme.ROBOT;
                } else if (isGoal) {
                    background = Theme.GOAL;
                } else if (isStart) {
                    background = Theme.START_DOCK;
                } else {
                    background = visualColor(overlay[r][c]);
                }
                g2.setColor(background);
                g2.fillRect(x, y, size, size);

                if (hasRobot) {
                    Icons.paint(g2, Icons.Kind.ROBOT, x + cell * 0.16, y + cell * 0.16, cell * 0.68, Color.WHITE);
                } else if (isGoal) {
                    Icons.paint(g2, Icons.Kind.FLAG, x + cell * 0.16, y + cell * 0.16, cell * 0.68, Color.WHITE);
                } else if (isStart) {
                    Icons.paint(g2, Icons.Kind.DOCK, x + cell * 0.16, y + cell * 0.16, cell * 0.68, Theme.ROBOT);
                }
            }
        }
        g2.dispose();
    }

    private static Color visualColor(CellVisual visual) {
        return switch (visual) {
            case FRONTIER -> Theme.FRONTIER;
            case VISITED -> Theme.VISITED;
            case CURRENT -> Theme.EXPANDING;
            case PATH -> Theme.PATH;
            default -> Theme.CELL;
        };
    }

    /** Obstacles look like shelving racks: dark slate with diagonal hatching. */
    private static void paintObstacle(Graphics2D g2, int x, int y, int size) {
        g2.setColor(Theme.OBSTACLE);
        g2.fillRect(x, y, size, size);
        Graphics2D clipped = (Graphics2D) g2.create();
        clipped.clipRect(x, y, size, size);
        clipped.setColor(new Color(255, 255, 255, 40));
        for (int k = -size; k < size * 2; k += 7) {
            clipped.drawLine(x + k, y + size, x + k + size, y);
        }
        clipped.dispose();
    }
}
