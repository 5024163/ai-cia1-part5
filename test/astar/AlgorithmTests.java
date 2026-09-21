package astar;

import astar.algorithm.AlgorithmRunner;
import astar.algorithm.OptimizedAStar;
import astar.algorithm.PathSearch;
import astar.algorithm.StandardAStar;
import astar.model.AlgorithmMode;
import astar.model.AlgorithmResult;
import astar.model.Position;
import astar.util.GridUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Automated checks for both algorithms. No test library is needed:
 * run it with the test script (test.sh / test.bat) or "java astar.AlgorithmTests".
 */
public class AlgorithmTests {
    private static int checks = 0;
    private static int failures = 0;

    public static void main(String[] args) {
        testBothAlgorithms("Standard A*", new StandardAStar());
        testBothAlgorithms("Optimized A*", new OptimizedAStar());
        testMetrics();

        System.out.println();
        if (failures == 0) {
            System.out.println("All " + checks + " checks passed.");
        } else {
            System.out.println(failures + " of " + checks + " checks FAILED.");
            System.exit(1);
        }
    }

    private static void testBothAlgorithms(String name, PathSearch search) {
        System.out.println("== " + name + " ==");

        // Straight path on an empty map
        Map m = parse("S....G");
        AlgorithmResult r = search.search(m.grid, m.start, m.goal);
        check(name + ": finds a straight path", r.found() && r.pathLength() == 5);
        check(name + ": straight path is valid", isValidPath(r, m));

        // Path around a wall
        m = parse("S.#....",
                  "..#.##.",
                  "..#..#.",
                  ".....#G");
        r = search.search(m.grid, m.start, m.goal);
        check(name + ": goes around a wall", r.found() && isValidPath(r, m));
        check(name + ": wall path is shortest (matches BFS)", r.pathLength() == bfsLength(m));

        // Goal walled off
        m = parse("S..#...",
                  "...#.G.",
                  "...#...");
        r = search.search(m.grid, m.start, m.goal);
        check(name + ": reports no path when goal is walled off", !r.found() && r.path().isEmpty());

        // Start equals goal
        boolean[][] empty = GridUtils.createEmptyGrid(3, 3);
        Position p = new Position(1, 1);
        r = search.search(empty, p, p);
        check(name + ": handles start equal to goal", r.found() && r.pathLength() == 0);

        // 300 random maps compared with a breadth-first search
        Random random = new Random(42);
        boolean allOk = true;
        for (int n = 0; n < 300; n++) {
            int size = 12;
            boolean[][] grid = new boolean[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    grid[i][j] = random.nextDouble() < 0.3;
                }
            }
            Position start = new Position(0, 0);
            Position goal = new Position(size - 1, size - 1);
            grid[0][0] = false;
            grid[size - 1][size - 1] = false;
            Map map = new Map(grid, start, goal);

            int expected = bfsLength(map);
            AlgorithmResult result = search.search(grid, start, goal);
            if (expected == -1) {
                allOk &= !result.found();
            } else {
                allOk &= result.found() && result.pathLength() == expected && isValidPath(result, map);
            }
        }
        check(name + ": 300 random maps match the true shortest length", allOk);
    }

    private static void testMetrics() {
        System.out.println("== Metrics ==");
        Map m = parse("S.....",
                      ".####.",
                      "......",
                      ".....G");
        boolean ok = true;
        for (AlgorithmMode mode : AlgorithmMode.values()) {
            AlgorithmRunner.Outcome o = AlgorithmRunner.run(mode, m.grid, m.start, m.goal);
            ok &= o.metrics().pathLength() == o.result().pathLength();
            ok &= o.metrics().nodesExpanded() == o.result().steps().size();
            ok &= o.metrics().executionTimeMs() >= 0;
            ok &= o.metrics().efficiency() > 0 && o.metrics().efficiency() <= 100;
        }
        check("Metrics are measured from the real run", ok);
    }

    // ---------- helpers ----------

    private record Map(boolean[][] grid, Position start, Position goal) {
    }

    /** Builds a map from text: S = start, G = goal, # = obstacle, . = free. */
    private static Map parse(String... lines) {
        boolean[][] grid = new boolean[lines.length][lines[0].length()];
        Position start = null;
        Position goal = null;
        for (int r = 0; r < lines.length; r++) {
            for (int c = 0; c < lines[r].length(); c++) {
                char ch = lines[r].charAt(c);
                if (ch == '#') {
                    grid[r][c] = true;
                } else if (ch == 'S') {
                    start = new Position(r, c);
                } else if (ch == 'G') {
                    goal = new Position(r, c);
                }
            }
        }
        return new Map(grid, start, goal);
    }

    /** Independent reference: breadth-first search gives the true shortest length (-1 = no path). */
    private static int bfsLength(Map m) {
        int rows = m.grid.length;
        int cols = m.grid[0].length;
        int[][] dist = new int[rows][cols];
        for (int[] row : dist) {
            java.util.Arrays.fill(row, -1);
        }
        Deque<Position> queue = new ArrayDeque<>();
        queue.add(m.start);
        dist[m.start.row()][m.start.col()] = 0;
        while (!queue.isEmpty()) {
            Position cur = queue.poll();
            if (cur.equals(m.goal)) {
                return dist[cur.row()][cur.col()];
            }
            for (Position n : GridUtils.getNeighbors(m.grid, cur)) {
                if (dist[n.row()][n.col()] == -1) {
                    dist[n.row()][n.col()] = dist[cur.row()][cur.col()] + 1;
                    queue.add(n);
                }
            }
        }
        return -1;
    }

    /** A valid path starts at start, ends at goal, moves one orthogonal step at a time and avoids obstacles. */
    private static boolean isValidPath(AlgorithmResult result, Map m) {
        List<Position> path = result.path();
        if (path.isEmpty() || !path.get(0).equals(m.start) || !path.get(path.size() - 1).equals(m.goal)) {
            return false;
        }
        for (int i = 0; i < path.size(); i++) {
            Position p = path.get(i);
            if (m.grid[p.row()][p.col()]) {
                return false;
            }
            if (i > 0) {
                Position q = path.get(i - 1);
                if (Math.abs(p.row() - q.row()) + Math.abs(p.col() - q.col()) != 1) {
                    return false;
                }
            }
        }
        return result.pathLength() == path.size() - 1;
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
