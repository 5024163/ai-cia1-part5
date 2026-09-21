package astar;

import astar.controller.SimulationController;
import astar.model.AlgorithmMode;
import astar.model.Position;
import astar.model.Speed;
import astar.model.Status;
import astar.model.Tool;

import javax.swing.SwingUtilities;

/**
 * Checks the simulator logic (editing, error messages, animation, metrics)
 * without opening a window. Run with the test script or "java astar.ControllerTests".
 */
public class ControllerTests {
    private static int checks = 0;
    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SimulationController c = new SimulationController();
        c.setSpeed(Speed.FAST);

        System.out.println("== Simulator logic ==");

        // Missing goal -> clear message
        c.setTool(Tool.GOAL);
        c.cellPressed(c.goal());           // clicking the goal again removes it
        c.findPath();
        check("no goal gives a clear message", c.status().kind() == Status.Kind.ERROR
                && c.status().message().equals("Please select a goal position."));

        // Goal cannot be placed on the start cell
        c.cellPressed(c.start());
        check("goal cannot be placed on the start cell",
                c.status().message().equals("Start and goal must be different cells.") && c.goal() == null);

        // Place the goal again, then close it in completely
        Position goal = new Position(7, 21);
        c.cellPressed(goal);
        check("goal can be placed again", goal.equals(c.goal()));
        c.setTool(Tool.OBSTACLE);
        int[][] around = {{6, 21}, {8, 21}, {7, 20}, {7, 22}};
        for (int[] a : around) {
            c.cellPressed(new Position(a[0], a[1]));
            c.editEnded();
        }
        c.findPath();
        check("blocked goal gives a clear message",
                c.status().message().equals("The goal is completely blocked by obstacles. Remove one next to it."));

        // Drag painting adds obstacles
        c.reset();
        c.setTool(Tool.OBSTACLE);
        c.cellPressed(new Position(2, 11));
        for (int r = 3; r <= 12; r++) {
            c.cellDragged(new Position(r, 11));
        }
        c.editEnded();
        int count = 0;
        for (boolean[] row : c.obstacles()) {
            for (boolean b : row) {
                if (b) count++;
            }
        }
        check("dragging paints a line of 11 obstacles", count == 11);

        // Full animation on the demo map, both algorithms
        c.loadDemoMap();
        for (AlgorithmMode mode : AlgorithmMode.values()) {
            c.setMode(mode);
            c.findPath();
            check(mode.label() + ": running flag is set while animating", c.isRunning());
            waitUntilFinished(c);
            check(mode.label() + ": finished with a path of 41 moves",
                    c.latest() != null && c.latest().found() && c.latest().pathLength() == 41);
            check(mode.label() + ": robot ended on the goal", c.goal().equals(c.robotPos()));
            check(mode.label() + ": result is stored for the comparison table", c.comparison().get(mode) != null);
        }
        check("Optimized A* expanded fewer nodes than Standard A* on the demo map",
                c.comparison().get(AlgorithmMode.OPTIMIZED).nodesExpanded()
                        < c.comparison().get(AlgorithmMode.STANDARD).nodesExpanded());

        // Editing the map clears old results
        c.setTool(Tool.OBSTACLE);
        c.cellPressed(new Position(0, 0));
        c.editEnded();
        check("editing the map clears the old results", c.latest() == null && c.comparison().isEmpty());

        // No path: wall the whole map in two
        c.reset();
        c.setTool(Tool.OBSTACLE);
        for (int r = 0; r < c.rows(); r++) {
            c.cellPressed(new Position(r, 12));
            c.editEnded();
        }
        c.findPath();
        waitUntilFinished(c);
        check("a full wall gives 'No path found'",
                c.status().message().equals("No path found. Try removing some obstacles.")
                        && c.latest() != null && !c.latest().found());

        // Stop button
        c.reset();
        c.setSpeed(Speed.SLOW);
        c.findPath();
        Thread.sleep(300);
        c.stop();
        check("Stop cancels the animation", !c.isRunning() && c.status().message().equals("Search stopped."));

        System.out.println();
        if (failures == 0) {
            System.out.println("All " + checks + " checks passed.");
        } else {
            System.out.println(failures + " of " + checks + " checks FAILED.");
            System.exit(1);
        }
        System.exit(0);
    }

    /** The animation runs on the Swing timer, so wait (up to 60 s) until it is done. */
    private static void waitUntilFinished(SimulationController c) throws Exception {
        for (int i = 0; i < 600; i++) {
            final boolean[] running = new boolean[1];
            SwingUtilities.invokeAndWait(() -> running[0] = c.isRunning());
            if (!running[0]) return;
            Thread.sleep(100);
        }
        throw new IllegalStateException("animation did not finish");
    }

    private static void check(String description, boolean condition) {
        checks++;
        if (condition) {
            System.out.println("  PASS  " + description);
        } else {
            failures++;
            System.out.println("  FAIL  " + description);
        }
    }
}
