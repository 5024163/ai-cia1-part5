package astar.algorithm;

import astar.model.AlgorithmResult;
import astar.model.Node;
import astar.model.Position;
import astar.model.SearchStep;
import astar.util.GridUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * OPTIMIZED A*  -  "Educational optimization inspired by the research objective"
 *
 * Same cost function f(n) = g(n) + h(n) and the same Manhattan heuristic as the
 * standard version, so it still returns a shortest path. What changes:
 *
 *  1. Binary-heap priority queue: taking the best node is O(log n), not an O(n) scan.
 *  2. Tie-breaking on h: among equal-f nodes, expand the one closest to the goal.
 *  3. No duplicate processing: a best-g table rejects worse routes, and stale
 *     duplicate heap entries are skipped when they are popped.
 *  4. Early exit: stop the moment the goal is generated. This is safe here because
 *     the goal is only ever generated from a node with the lowest f in the open
 *     set and h = 1 next to the goal, so its cost is already the optimum.
 *
 * NOTE: this is a simple educational optimization. It does NOT reproduce the
 * exact method of the research paper, and it does not promise fewer expansions
 * on every possible map.
 */
public class OptimizedAStar implements PathSearch {
    private static final int STEP_COST = 1;

    /**
     * Priority order for the open set:
     *   1. lowest f first (this is what makes it A*)
     *   2. if f is equal, prefer the LOWER h, i.e. the node that looks closer to the goal.
     *      This keeps the search "pointing" at the goal instead of spreading over
     *      every cell that has the same f.
     */
    private static final Comparator<Node> BY_F_THEN_H =
            Comparator.<Node>comparingInt(n -> n.f).thenComparingInt(n -> n.h);

    @Override
    public AlgorithmResult search(boolean[][] grid, Position start, Position goal, Heuristic heuristic) {
        int rows = grid.length;
        int cols = grid[0].length;

        if (start.equals(goal)) {
            return new AlgorithmResult(true, List.of(start), 0, 0, Collections.emptyList());
        }

        // Best known g for every cell (MAX_VALUE = not discovered yet).
        int[][] bestG = new int[rows][cols];
        for (int[] row : bestG) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        boolean[][] closed = new boolean[rows][cols];

        // 1. Open set = binary min-heap ordered by BY_F_THEN_H.
        MinHeap<Node> open = new MinHeap<>(BY_F_THEN_H);
        bestG[start.row()][start.col()] = 0;
        open.push(new Node(start, 0, heuristic.estimate(start, goal), null));

        List<SearchStep> steps = new ArrayList<>();
        int nodesExpanded = 0;

        while (open.size() > 0) {
            Node current = open.pop(); // lowest f (ties: lowest h)
            int row = current.position.row();
            int col = current.position.col();

            // 3. Skip stale duplicates: this cell was already expanded via a cheaper entry.
            if (closed[row][col]) {
                continue;
            }

            closed[row][col] = true;
            nodesExpanded++;
            List<Position> generated = new ArrayList<>();

            for (Position neighborPos : GridUtils.getNeighbors(grid, current.position)) {
                if (closed[neighborPos.row()][neighborPos.col()]) {
                    continue;
                }

                int tentativeG = current.g + STEP_COST;
                // Only continue if this is a better route than any found before.
                if (tentativeG >= bestG[neighborPos.row()][neighborPos.col()]) {
                    continue;
                }
                bestG[neighborPos.row()][neighborPos.col()] = tentativeG;

                Node node = new Node(neighborPos, tentativeG, heuristic.estimate(neighborPos, goal), current);

                // 4. Early exit as soon as the goal is reached.
                if (neighborPos.equals(goal)) {
                    steps.add(new SearchStep(current.position, generated));
                    List<Position> path = NodeUtils.reconstructPath(node);
                    return new AlgorithmResult(true, path, path.size() - 1, nodesExpanded, steps);
                }

                // A better route to a cell already in the heap is pushed as a NEW entry;
                // the old (worse) entry becomes stale and is skipped when popped.
                open.push(node);
                generated.add(neighborPos);
            }

            steps.add(new SearchStep(current.position, generated));
        }

        return new AlgorithmResult(false, Collections.emptyList(), 0, nodesExpanded, steps);
    }
}
