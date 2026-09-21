package astar.algorithm;

import astar.model.AlgorithmResult;
import astar.model.Position;

/** Common interface of both search algorithms. */
public interface PathSearch {
    /**
     * @param grid obstacles[row][col] == true means the cell is blocked
     */
    AlgorithmResult search(boolean[][] grid, Position start, Position goal, Heuristic heuristic);

    default AlgorithmResult search(boolean[][] grid, Position start, Position goal) {
        return search(grid, start, goal, Heuristics.MANHATTAN);
    }
}
