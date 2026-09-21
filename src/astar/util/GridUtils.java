package astar.util;

import astar.model.CellVisual;
import astar.model.Position;
import astar.model.SearchStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Helper methods for the grid: neighbours, demo/random maps and animation colours. */
public final class GridUtils {
    /** Fraction of cells filled by the "Random Obstacles" button. */
    public static final double RANDOM_OBSTACLE_DENSITY = 0.28;

    /** The four moves the robot may make (no diagonals): up, down, left, right. */
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private static final Random RANDOM = new Random();

    private GridUtils() {
    }

    public static boolean isInBounds(boolean[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
    }

    /**
     * Returns the neighbouring cells the robot can actually move into:
     * inside the grid and NOT an obstacle.
     */
    public static List<Position> getNeighbors(boolean[][] grid, Position pos) {
        List<Position> neighbors = new ArrayList<>(4);
        for (int[] d : DIRECTIONS) {
            int r = pos.row() + d[0];
            int c = pos.col() + d[1];
            if (isInBounds(grid, r, c) && !grid[r][c]) {
                neighbors.add(new Position(r, c));
            }
        }
        return neighbors;
    }

    // ---------- Grid creation ----------

    public static boolean[][] createEmptyGrid(int rows, int cols) {
        return new boolean[rows][cols];
    }

    public static CellVisual[][] createOverlay(int rows, int cols) {
        CellVisual[][] overlay = new CellVisual[rows][cols];
        for (CellVisual[] row : overlay) {
            Arrays.fill(row, CellVisual.NONE);
        }
        return overlay;
    }

    public static Position defaultStart(int rows) {
        return new Position(rows / 2, 2);
    }

    public static Position defaultGoal(int rows, int cols) {
        return new Position(rows / 2, cols - 3);
    }

    /**
     * Two staggered walls: the robot must go down around the first wall
     * and up around the second one. Good for demonstrations.
     */
    public static boolean[][] createDemoObstacles(int rows, int cols) {
        boolean[][] grid = createEmptyGrid(rows, cols);
        int wall1 = (int) Math.round(cols * 0.33);
        int wall2 = (int) Math.round(cols * 0.66);
        for (int r = 0; r < rows; r++) {
            if (r < rows - 2) {
                grid[r][wall1] = true; // gap at the bottom
            }
            if (r > 1) {
                grid[r][wall2] = true; // gap at the top
            }
        }
        return grid;
    }

    /** True if a route exists (simple breadth-first flood fill, used only for validation). */
    public static boolean isReachable(boolean[][] grid, Position start, Position goal) {
        boolean[][] seen = new boolean[grid.length][grid[0].length];
        List<Position> queue = new ArrayList<>();
        queue.add(start);
        seen[start.row()][start.col()] = true;
        for (int i = 0; i < queue.size(); i++) {
            Position cur = queue.get(i);
            if (cur.equals(goal)) {
                return true;
            }
            for (Position n : getNeighbors(grid, cur)) {
                if (!seen[n.row()][n.col()]) {
                    seen[n.row()][n.col()] = true;
                    queue.add(n);
                }
            }
        }
        return false;
    }

    /** True when every neighbour of the cell is blocked or off the grid. */
    public static boolean isEnclosed(boolean[][] grid, Position pos) {
        return getNeighbors(grid, pos).isEmpty();
    }

    /**
     * Random obstacle layout. It retries a few times so that (when start and goal
     * exist) a route is normally still possible, which makes for a nicer demo.
     */
    public static boolean[][] generateRandomObstacles(int rows, int cols, Position start, Position goal) {
        double[] densities = {RANDOM_OBSTACLE_DENSITY, RANDOM_OBSTACLE_DENSITY * 0.75, RANDOM_OBSTACLE_DENSITY * 0.5};
        for (double density : densities) {
            for (int attempt = 0; attempt < 25; attempt++) {
                boolean[][] grid = new boolean[rows][cols];
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        grid[r][c] = RANDOM.nextDouble() < density;
                    }
                }
                if (start != null) {
                    grid[start.row()][start.col()] = false;
                }
                if (goal != null) {
                    grid[goal.row()][goal.col()] = false;
                }
                if (start == null || goal == null || isReachable(grid, start, goal)) {
                    return grid;
                }
            }
        }
        return createEmptyGrid(rows, cols);
    }

    public static boolean[][] copyGrid(boolean[][] grid) {
        boolean[][] copy = new boolean[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }

    // ---------- Animation helpers ----------

    /** Applies one search step to the colour overlay (changes `overlay`). */
    public static void applySearchStep(CellVisual[][] overlay, SearchStep step, Position previousCurrent) {
        if (previousCurrent != null) {
            overlay[previousCurrent.row()][previousCurrent.col()] = CellVisual.VISITED;
        }
        overlay[step.current().row()][step.current().col()] = CellVisual.CURRENT;
        for (Position g : step.generated()) {
            if (overlay[g.row()][g.col()] == CellVisual.NONE) {
                overlay[g.row()][g.col()] = CellVisual.FRONTIER;
            }
        }
    }

    /** Builds the finished picture (explored area + final path) without animating. */
    public static CellVisual[][] buildFinalOverlay(int rows, int cols, List<SearchStep> steps, List<Position> path) {
        CellVisual[][] overlay = createOverlay(rows, cols);
        for (SearchStep step : steps) {
            for (Position g : step.generated()) {
                if (overlay[g.row()][g.col()] == CellVisual.NONE) {
                    overlay[g.row()][g.col()] = CellVisual.FRONTIER;
                }
            }
            overlay[step.current().row()][step.current().col()] = CellVisual.VISITED;
        }
        for (Position p : path) {
            overlay[p.row()][p.col()] = CellVisual.PATH;
        }
        return overlay;
    }
}
