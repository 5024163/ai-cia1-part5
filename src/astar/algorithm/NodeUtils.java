package astar.algorithm;

import astar.model.Node;
import astar.model.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class NodeUtils {
    private NodeUtils() {
    }

    /** Walks the parent links from the goal back to the start, then reverses the list. */
    static List<Position> reconstructPath(Node goalNode) {
        List<Position> path = new ArrayList<>();
        for (Node node = goalNode; node != null; node = node.parent) {
            path.add(node.position);
        }
        Collections.reverse(path);
        return path;
    }
}
