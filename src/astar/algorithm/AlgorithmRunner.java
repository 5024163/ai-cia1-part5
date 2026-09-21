package astar.algorithm;

import astar.model.AlgorithmMode;
import astar.model.AlgorithmResult;
import astar.model.Metrics;
import astar.model.Position;

/** Runs a chosen algorithm and measures it. */
public final class AlgorithmRunner {
    /**
     * Execution time is the average of this many repeated runs.
     * A single run takes well under a millisecond, which is too small to time reliably.
     */
    public static final int TIMING_RUNS = 20;

    private static final PathSearch STANDARD = new StandardAStar();
    private static final PathSearch OPTIMIZED = new OptimizedAStar();

    private AlgorithmRunner() {
    }

    public record Outcome(AlgorithmResult result, Metrics metrics) {
    }

    /**
     * - pathLength     = number of moves in the path found by the algorithm
     * - nodesExpanded  = nodes taken from the open set and expanded (counted inside the search)
     * - executionTime  = average compute time of the search over TIMING_RUNS repeats.
     *                    (The first run is used for the result and also warms up the JVM.)
     * - efficiency     = pathLength / nodesExpanded x 100.
     *                    "Of all the nodes the robot had to expand, what percentage lay on
     *                    the final route?" 100% means no exploration was wasted.
     *
     * Everything here comes from the real execution; nothing is hard-coded.
     */
    public static Outcome run(AlgorithmMode mode, boolean[][] grid, Position start, Position goal) {
        PathSearch search = mode == AlgorithmMode.STANDARD ? STANDARD : OPTIMIZED;

        AlgorithmResult result = search.search(grid, start, goal);

        long t0 = System.nanoTime();
        for (int i = 0; i < TIMING_RUNS; i++) {
            search.search(grid, start, goal);
        }
        double executionTimeMs = (System.nanoTime() - t0) / 1_000_000.0 / TIMING_RUNS;

        double efficiency = result.found() && result.nodesExpanded() > 0
                ? (double) result.pathLength() / result.nodesExpanded() * 100.0
                : 0.0;

        Metrics metrics = new Metrics(mode, result.found(), result.pathLength(),
                result.nodesExpanded(), executionTimeMs, efficiency);
        return new Outcome(result, metrics);
    }
}
