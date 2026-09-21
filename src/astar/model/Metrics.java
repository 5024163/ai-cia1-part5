package astar.model;

/**
 * Measured values shown in the results panel and the comparison table.
 *
 * @param executionTimeMs average compute time of the search itself (see AlgorithmRunner)
 * @param efficiency      pathLength / nodesExpanded x 100
 */
public record Metrics(AlgorithmMode mode, boolean found, int pathLength, int nodesExpanded,
                      double executionTimeMs, double efficiency) {
}
