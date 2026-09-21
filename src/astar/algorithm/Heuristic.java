package astar.algorithm;

import astar.model.Position;

/** Heuristic function h(n): an estimate of the cost between two cells. */
@FunctionalInterface
public interface Heuristic {
    int estimate(Position from, Position to);
}
