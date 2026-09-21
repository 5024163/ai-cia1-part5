package astar.model;

import java.util.List;

/**
 * Everything one A* run returns.
 *
 * @param found         true if a path exists
 * @param path          cells from start to goal (empty when no path exists)
 * @param pathLength    number of moves on the path (0 when no path)
 * @param nodesExpanded number of nodes taken from the open set and expanded
 * @param steps         expansion order, used for the search animation
 */
public record AlgorithmResult(boolean found, List<Position> path, int pathLength,
                              int nodesExpanded, List<SearchStep> steps) {
}
