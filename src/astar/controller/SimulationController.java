package astar.controller;

import astar.algorithm.AlgorithmRunner;
import astar.model.AlgorithmMode;
import astar.model.AlgorithmResult;
import astar.model.CellVisual;
import astar.model.GridSize;
import astar.model.Metrics;
import astar.model.Position;
import astar.model.SearchStep;
import astar.model.Speed;
import astar.model.Status;
import astar.model.Tool;
import astar.util.GridUtils;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;

/**
 * The "controller": it keeps all the state of the simulator (map, start, goal,
 * chosen algorithm, colours, results) and plays the animation.
 *
 * The screen classes only display this state and forward the user's clicks here.
 * The A* algorithms themselves are NOT in this class: they live in astar.algorithm
 * and know nothing about the screen.
 */
public class SimulationController {

    /** Implemented by the main window so it can redraw when something changes. */
    public interface Listener {
        /** Only cell colours / robot position changed (cheap redraw of the grid). */
        void onVisualChange();

        /** Anything else changed (buttons, results, comparison, ...). */
        void onStateChange();
    }

    private static final Status READY =
            new Status(Status.Kind.IDLE, "Ready. Add some obstacles if you like, then press Find Path.");
    private static final String NO_PATH_MESSAGE = "No path found. Try removing some obstacles.";

    // ----- environment -----
    private GridSize size = GridSize.MEDIUM;
    private boolean[][] obstacles = GridUtils.createEmptyGrid(size.rows(), size.cols());
    private Position start = GridUtils.defaultStart(size.rows());
    private Position goal = GridUtils.defaultGoal(size.rows(), size.cols());

    // ----- user settings -----
    private AlgorithmMode mode = AlgorithmMode.OPTIMIZED;
    private Tool tool = Tool.OBSTACLE;
    private Speed speed = Speed.NORMAL;

    // ----- simulation output -----
    private CellVisual[][] overlay = GridUtils.createOverlay(size.rows(), size.cols());
    private Position robotPos = null;
    private boolean running = false;
    private Status status = READY;
    private Metrics latest = null;
    private final Map<AlgorithmMode, Metrics> comparison = new EnumMap<>(AlgorithmMode.class);

    // ----- internals -----
    private Listener listener;
    private Timer timer;
    private int runId = 0;          // changes whenever an animation is cancelled
    private Boolean paintValue = null; // true = adding obstacles, false = erasing, null = not painting

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    // ==================== getters (used by the screen) ====================

    public GridSize size() { return size; }
    public int rows() { return size.rows(); }
    public int cols() { return size.cols(); }
    public boolean[][] obstacles() { return obstacles; }
    public Position start() { return start; }
    public Position goal() { return goal; }
    public AlgorithmMode mode() { return mode; }
    public Tool tool() { return tool; }
    public Speed speed() { return speed; }
    public CellVisual[][] overlay() { return overlay; }
    public Position robotPos() { return robotPos; }
    public boolean isRunning() { return running; }
    public Status status() { return status; }
    public Metrics latest() { return latest; }
    public Map<AlgorithmMode, Metrics> comparison() { return comparison; }

    // ==================== simple setters ====================

    public void setMode(AlgorithmMode mode) {
        if (running) return;
        this.mode = mode;
        fireState();
    }

    public void setTool(Tool tool) {
        if (running) return;
        this.tool = tool;
        fireState();
    }

    /** Speed can be changed even while a search is running. */
    public void setSpeed(Speed speed) {
        this.speed = speed;
        fireState();
    }

    // ==================== helpers ====================

    private void fireState() {
        if (listener != null) listener.onStateChange();
    }

    private void fireVisual() {
        if (listener != null) listener.onVisualChange();
    }

    private void cancelAnimation() {
        runId++;
        if (timer != null) timer.stop();
    }

    /** Removes the search colours, robot movement and metrics (keeps the map). */
    private void clearVisuals() {
        cancelAnimation();
        running = false;
        overlay = GridUtils.createOverlay(rows(), cols());
        robotPos = null;
        latest = null;
    }

    /** Called after any map edit: old results no longer match the map. */
    private void clearResults() {
        clearVisuals();
        comparison.clear();
        status = READY;
    }

    /** Replaces the whole environment (used by Reset, Demo map and size changes). */
    private void loadLayout(GridSize newSize, boolean[][] grid, Position newStart, Position newGoal, String message) {
        cancelAnimation();
        running = false;
        size = newSize;
        obstacles = grid;
        start = newStart;
        goal = newGoal;
        overlay = GridUtils.createOverlay(size.rows(), size.cols());
        robotPos = null;
        latest = null;
        comparison.clear();
        status = new Status(Status.Kind.INFO, message);
        fireState();
    }

    // ==================== grid editing ====================

    private void placeStart(Position pos) {
        if (pos.equals(goal)) {
            status = new Status(Status.Kind.ERROR, "Start and goal must be different cells.");
            return;
        }
        clearResults();
        if (pos.equals(start)) {
            start = null;
            status = new Status(Status.Kind.INFO, "Start removed. Click a cell to place the robot again.");
            return;
        }
        start = pos;
        obstacles[pos.row()][pos.col()] = false; // placing on an obstacle clears it
    }

    private void placeGoal(Position pos) {
        if (pos.equals(start)) {
            status = new Status(Status.Kind.ERROR, "Start and goal must be different cells.");
            return;
        }
        clearResults();
        if (pos.equals(goal)) {
            goal = null;
            status = new Status(Status.Kind.INFO, "Goal removed. Click a cell to place the goal again.");
            return;
        }
        goal = pos;
        obstacles[pos.row()][pos.col()] = false;
    }

    private void setObstacle(Position pos, boolean value) {
        if (pos.equals(start) || pos.equals(goal)) return;
        if (obstacles[pos.row()][pos.col()] == value) return;
        obstacles[pos.row()][pos.col()] = value;
        clearResults();
    }

    /** Mouse pressed on a cell. */
    public void cellPressed(Position pos) {
        if (running) return;
        switch (tool) {
            case START -> placeStart(pos);
            case GOAL -> placeGoal(pos);
            case OBSTACLE -> {
                if (pos.equals(start) || pos.equals(goal)) return;
                // The first cell decides whether this drag adds or erases obstacles.
                paintValue = !obstacles[pos.row()][pos.col()];
                setObstacle(pos, paintValue);
            }
        }
        fireState();
    }

    /** Mouse dragged over a cell while the button is held. */
    public void cellDragged(Position pos) {
        if (running || tool != Tool.OBSTACLE || paintValue == null) return;
        boolean before = obstacles[pos.row()][pos.col()];
        setObstacle(pos, paintValue);
        if (before != obstacles[pos.row()][pos.col()]) fireState();
    }

    /** Mouse released. */
    public void editEnded() {
        paintValue = null;
    }

    // ==================== running the algorithm ====================

    private record InputCheck(Position start, Position goal, Status error) {
    }

    /** Error handling: make sure the request makes sense before searching. */
    private InputCheck checkInputs() {
        if (start == null) return fail("Please select a start position.");
        if (goal == null) return fail("Please select a goal position.");
        if (start.equals(goal)) return fail("Start and goal are the same cell. Move one of them.");
        if (GridUtils.isEnclosed(obstacles, goal))
            return fail("The goal is completely blocked by obstacles. Remove one next to it.");
        if (GridUtils.isEnclosed(obstacles, start))
            return fail("The robot is boxed in by obstacles. Remove one next to it.");
        return new InputCheck(start, goal, null);
    }

    private static InputCheck fail(String message) {
        return new InputCheck(null, null, new Status(Status.Kind.ERROR, message));
    }

    private static String successMessage(Metrics m) {
        return "Path found: " + m.pathLength() + " moves, " + m.nodesExpanded()
                + " nodes expanded (" + m.mode().label() + ").";
    }

    public void findPath() {
        if (running) return;
        InputCheck check = checkInputs();
        if (check.error() != null) {
            status = check.error();
            fireState();
            return;
        }
        AlgorithmRunner.Outcome outcome = AlgorithmRunner.run(mode, obstacles, check.start(), check.goal());
        playAnimation(outcome.result(), outcome.metrics());
    }

    /** Animates the search first, then the robot driving along the final path. */
    private void playAnimation(AlgorithmResult result, Metrics metrics) {
        cancelAnimation();
        running = true;
        latest = null; // metrics appear when the animation finishes
        robotPos = null;
        overlay = GridUtils.createOverlay(rows(), cols());
        status = new Status(Status.Kind.RUNNING, "Exploring the grid with " + metrics.mode().label() + "...");
        fireState();

        Animation animation = new Animation(runId, result, metrics);
        timer = new Timer(0, animation);
        timer.setRepeats(false); // each frame schedules the next one, so speed changes apply at once
        animation.timer = timer;
        timer.start();
    }

    /** One frame of the animation is drawn every time actionPerformed runs. */
    private final class Animation implements ActionListener {
        private final int id;
        private final AlgorithmResult result;
        private final Metrics metrics;
        private Timer timer;
        private int stepIndex = 0;
        private int pathIndex = 0;
        private Position previous = null;

        Animation(int id, AlgorithmResult result, Metrics metrics) {
            this.id = id;
            this.result = result;
            this.metrics = metrics;
        }

        private void next(int delayMs) {
            timer.setInitialDelay(delayMs);
            timer.restart();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (id != runId) return; // cancelled (Stop, edit, reset...)

            // Phase 1: show the nodes being expanded, one per frame.
            if (stepIndex < result.steps().size()) {
                SearchStep step = result.steps().get(stepIndex++);
                GridUtils.applySearchStep(overlay, step, previous);
                previous = step.current();
                fireVisual();
                next(speed.searchDelay());
                return;
            }

            if (previous != null) {
                overlay[previous.row()][previous.col()] = CellVisual.VISITED;
                previous = null;
            }

            if (!result.found()) {
                fireVisual();
                finish();
                return;
            }

            // Phase 2: the robot drives along the path, leaving the route behind it.
            if (pathIndex == 0) {
                status = new Status(Status.Kind.RUNNING, "Path found. The robot is driving along it...");
                fireState();
            }
            if (pathIndex < result.path().size()) {
                Position cell = result.path().get(pathIndex++);
                overlay[cell.row()][cell.col()] = CellVisual.PATH;
                robotPos = cell;
                fireVisual();
                next(speed.pathDelay());
                return;
            }

            finish();
        }

        private void finish() {
            running = false;
            latest = metrics;
            comparison.put(metrics.mode(), metrics);
            status = metrics.found()
                    ? new Status(Status.Kind.SUCCESS, successMessage(metrics))
                    : new Status(Status.Kind.ERROR, NO_PATH_MESSAGE);
            fireState();
        }
    }

    /** Runs both algorithms instantly (no animation) so the comparison table can be filled. */
    public void compareBoth() {
        if (running) return;
        InputCheck check = checkInputs();
        if (check.error() != null) {
            status = check.error();
            fireState();
            return;
        }
        AlgorithmRunner.Outcome standard = AlgorithmRunner.run(AlgorithmMode.STANDARD, obstacles, check.start(), check.goal());
        AlgorithmRunner.Outcome optimized = AlgorithmRunner.run(AlgorithmMode.OPTIMIZED, obstacles, check.start(), check.goal());
        AlgorithmRunner.Outcome shown = mode == AlgorithmMode.STANDARD ? standard : optimized;

        cancelAnimation();
        comparison.clear();
        comparison.put(AlgorithmMode.STANDARD, standard.metrics());
        comparison.put(AlgorithmMode.OPTIMIZED, optimized.metrics());
        latest = shown.metrics();
        overlay = GridUtils.buildFinalOverlay(rows(), cols(), shown.result().steps(), shown.result().path());
        robotPos = shown.result().found() ? check.goal() : null;
        status = shown.result().found()
                ? new Status(Status.Kind.SUCCESS,
                        "Both algorithms ran on this map. The grid shows the " + mode.label() + " search.")
                : new Status(Status.Kind.ERROR, NO_PATH_MESSAGE);
        fireState();
    }

    // ==================== buttons ====================

    public void stop() {
        clearVisuals();
        status = new Status(Status.Kind.INFO, "Search stopped.");
        fireState();
    }

    public void clearPath() {
        if (running) return;
        clearVisuals();
        status = READY;
        fireState();
    }

    public void clearObstacles() {
        if (running) return;
        obstacles = GridUtils.createEmptyGrid(rows(), cols());
        clearResults();
        status = new Status(Status.Kind.INFO, "All obstacles removed.");
        fireState();
    }

    public void randomObstacles() {
        if (running) return;
        obstacles = GridUtils.generateRandomObstacles(rows(), cols(), start, goal);
        clearResults();
        status = new Status(Status.Kind.INFO, "Random obstacles generated. Press Find Path to plan a route.");
        fireState();
    }

    public void loadDemoMap() {
        if (running) return;
        loadLayout(size, GridUtils.createDemoObstacles(rows(), cols()),
                GridUtils.defaultStart(rows()), GridUtils.defaultGoal(rows(), cols()),
                "Demo map loaded: two staggered walls. Press Find Path.");
    }

    public void reset() {
        if (running) return;
        loadLayout(size, GridUtils.createEmptyGrid(rows(), cols()),
                GridUtils.defaultStart(rows()), GridUtils.defaultGoal(rows(), cols()),
                "Grid reset to an empty workspace.");
    }

    public void changeSize(GridSize newSize) {
        if (running || newSize == size) return;
        loadLayout(newSize, GridUtils.createEmptyGrid(newSize.rows(), newSize.cols()),
                GridUtils.defaultStart(newSize.rows()), GridUtils.defaultGoal(newSize.rows(), newSize.cols()),
                "Grid resized to " + newSize.cols() + " x " + newSize.rows() + ".");
    }
}
