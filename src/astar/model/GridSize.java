package astar.model;

public enum GridSize {
    SMALL("Small", 10, 16),
    MEDIUM("Medium", 14, 24),
    LARGE("Large", 18, 32);

    private final String label;
    private final int rows;
    private final int cols;

    GridSize(String label, int rows, int cols) {
        this.label = label;
        this.rows = rows;
        this.cols = cols;
    }

    public String label() {
        return label;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }
}
