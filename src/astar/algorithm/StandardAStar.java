package astar.algorithm;

import astar.model.AlgorithmResult;
import astar.model.Node;
import astar.model.Position;
import astar.model.SearchStep;
import astar.util.GridUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * STANDARD A* (textbook version)
 *
 *   f(n) = g(n) + h(n)
 *
 * Data structures:
 *   openList - a plain list of nodes waiting to be expanded.
 *              The lowest-f node is found by scanning the whole list (O(n)).
 *   closed   - a 2D array marking cells that were already expanded.
 *
 * Ties: when several nodes have the same lowest f, the one that has been
 * waiting longest in the list is chosen (the first one found by the scan).
 */
public class StandardAStar implements PathSearch {
    /** Every move (up/down/left/right) costs 1. */
    private static final int STEP_COST = 1;

    @Override
    public AlgorithmResult search(boolean[][] grid, Position start, Position goal, Heuristic heuristic) {
        int rows = grid.length;
        int cols = grid[0].length;

        // 1. Initialise the open set with the start node (g = 0, h = estimate to goal).
        Node startNode = new Node(start, 0, heuristic.estimate(start, goal), null);
        List<Node> openList = new ArrayList<>();
        openList.add(startNode);

        // Lets us find the open node for a cell without scanning the list.
        Node[][] openLookup = new Node[rows][cols];
        openLookup[start.row()][start.col()] = startNode;

        // 2. Initialise the closed (already expanded) set.
        boolean[][] closed = new boolean[rows][cols];

        List<SearchStep> steps = new ArrayList<>();
        int nodesExpanded = 0;

        while (!openList.isEmpty()) {
            // 4. Select the node with the lowest f value (linear scan of the open list).
            int bestIndex = 0;
            for (int i = 1; i < openList.size(); i++) {
                if (openList.get(i).f < openList.get(bestIndex).f) {
                    bestIndex = i;
                }
            }
            Node current = openList.remove(bestIndex);
            openLookup[current.position.row()][current.position.col()] = null;

            // 9. Stop when the goal is reached, then 10. rebuild the path from the parent links.
            if (current.position.equals(goal)) {
                List<Position> path = NodeUtils.reconstructPath(current);
                return new AlgorithmResult(true, path, path.size() - 1, nodesExpanded, steps);
            }

            closed[current.position.row()][current.position.col()] = true;
            nodesExpanded++;
            List<Position> generated = new ArrayList<>();

            // 5. Expand the valid neighbours (obstacles and grid edges are already excluded).
            for (Position neighborPos : GridUtils.getNeighbors(grid, current.position)) {
                if (closed[neighborPos.row()][neighborPos.col()]) {
                    continue;
                }

                // 3. g = cost so far, h = heuristic to the goal, f = g + h
                int tentativeG = current.g + STEP_COST;
                Node existing = openLookup[neighborPos.row()][neighborPos.col()];

                if (existing == null) {
                    // Newly discovered cell: add it to the open list.
                    Node node = new Node(neighborPos, tentativeG, heuristic.estimate(neighborPos, goal), current);
                    openList.add(node);
                    openLookup[neighborPos.row()][neighborPos.col()] = node;
                    generated.add(neighborPos);
                } else if (tentativeG < existing.g) {
                    // 7. Better route to a cell that is already waiting: update its cost and parent (8).
                    existing.g = tentativeG;
                    existing.f = tentativeG + existing.h;
                    existing.parent = current;
                }
            }

            steps.add(new SearchStep(current.position, generated));
        }

        // The open list is empty and the goal was never reached: no path exists.
        return new AlgorithmResult(false, Collections.emptyList(), 0, nodesExpanded, steps);
    }
}
