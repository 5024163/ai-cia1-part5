package astar.model;

/** Delay (in milliseconds) between animation frames. Deliberately not too fast. */
public enum Speed {
    SLOW("Slow", 110, 130),
    NORMAL("Normal", 40, 70),
    FAST("Fast", 10, 30);

    private final String label;
    private final int searchDelay;
    private final int pathDelay;

    Speed(String label, int searchDelay, int pathDelay) {
        this.label = label;
        this.searchDelay = searchDelay;
        this.pathDelay = pathDelay;
    }

    public String label() {
        return label;
    }

    public int searchDelay() {
        return searchDelay;
    }

    public int pathDelay() {
        return pathDelay;
    }
}
