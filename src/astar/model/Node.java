package astar.model;

/**
 * A node used inside the A* search.
 *
 * g      = actual cost from the start to this node (number of moves so far)
 * h      = heuristic estimate of the remaining cost to the goal
 * f      = g + h (estimated total cost of a path through this node)
 * parent = the node we came from (used to rebuild the final path)
 */
public final class Node {
    public final Position position;
    public int g;
    public final int h;
    public int f;
    public Node parent;

    public Node(Position position, int g, int h, Node parent) {
        this.position = position;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.parent = parent;
    }
}
