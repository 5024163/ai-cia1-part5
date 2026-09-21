package astar.model;

import java.util.List;

/**
 * One expansion step of the search. It is recorded so the screen can replay it.
 *
 * @param current   the node that was taken from the open set and expanded
 * @param generated new nodes that were added to the open set during this step
 */
public record SearchStep(Position current, List<Position> generated) {
}
