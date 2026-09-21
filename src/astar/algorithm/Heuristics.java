package astar.algorithm;

public final class Heuristics {
    private Heuristics() {
    }

    /**
     * Manhattan distance:  h(n) = |x1 - x2| + |y1 - y2|
     *
     * It is the natural heuristic for a robot that moves only up/down/left/right:
     *  - It never over-estimates the real cost (it is "admissible"),
     *    so A* is guaranteed to return a shortest path.
     *  - It is also "consistent", so a cell never has to be re-opened after it is closed.
     */
    public static final Heuristic MANHATTAN = (from, to) ->
            Math.abs(from.row() - to.row()) + Math.abs(from.col() - to.col());
}
