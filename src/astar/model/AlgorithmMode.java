package astar.model;

public enum AlgorithmMode {
    STANDARD("Standard A*"),
    OPTIMIZED("Optimized A*");

    private final String label;

    AlgorithmMode(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
