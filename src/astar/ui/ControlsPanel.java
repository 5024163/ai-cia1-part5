package astar.ui;

import astar.controller.SimulationController;
import astar.model.AlgorithmMode;
import astar.model.GridSize;
import astar.model.Speed;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/** The "Control panel" card: algorithm, Find Path / Stop, action buttons, speed and grid size. */
public class ControlsPanel extends CardPanel {
    private static final int INNER_WIDTH = 336;

    private final SimulationController controller;
    private final SegmentedControl<AlgorithmMode> algorithm;
    private final WrapText caption;
    private final FlatButton findButton;
    private final List<FlatButton> actionButtons = new ArrayList<>();
    private final SegmentedControl<Speed> speed;
    private final SegmentedControl<GridSize> gridSize;

    public ControlsPanel(SimulationController controller, int width) {
        super(new java.awt.BorderLayout(), width, 20, 20);
        this.controller = controller;
        setName("controls");

        JPanel col = Ui.column();
        add(col, java.awt.BorderLayout.CENTER);

        col.add(Ui.left(Ui.label("Control panel", Font.BOLD, 20f, Theme.INK)));
        Ui.gap(col, 14);

        // ----- algorithm -----
        col.add(Ui.left(Ui.label("Algorithm", Font.BOLD, 14f, Theme.INK_SOFT)));
        Ui.gap(col, 6);
        algorithm = new SegmentedControl<>(List.of(
                new SegmentedControl.Option<>(AlgorithmMode.STANDARD, AlgorithmMode.STANDARD.label()),
                new SegmentedControl.Option<>(AlgorithmMode.OPTIMIZED, AlgorithmMode.OPTIMIZED.label())),
                controller.mode(), controller::setMode);
        col.add(Ui.left(algorithm));
        Ui.gap(col, 8);
        caption = Ui.wrapped(INNER_WIDTH, "", Font.PLAIN, 13f, Theme.MUTED);
        col.add(Ui.left(caption));
        Ui.gap(col, 14);

        // ----- Find Path / Stop -----
        findButton = new FlatButton("Find Path", FlatButton.Variant.PRIMARY);
        findButton.setFont(Theme.font(Font.BOLD, 16f));
        findButton.setPreferredSize(new Dimension(INNER_WIDTH, 46));
        findButton.addActionListener(e -> {
            if (controller.isRunning()) {
                controller.stop();
            } else {
                controller.findPath();
            }
        });
        col.add(Ui.left(findButton));
        Ui.gap(col, 12);

        // ----- action buttons -----
        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setOpaque(false);
        grid.add(action("Clear Path", controller::clearPath));
        grid.add(action("Clear Obstacles", controller::clearObstacles));
        grid.add(action("Random Obstacles", controller::randomObstacles));
        grid.add(action("Reset", controller::reset));
        grid.setPreferredSize(new Dimension(INNER_WIDTH, 92));
        col.add(Ui.left(grid));
        Ui.gap(col, 8);
        FlatButton demo = action("Load Demo Map", controller::loadDemoMap);
        demo.setPreferredSize(new Dimension(INNER_WIDTH, 42));
        col.add(Ui.left(demo));
        Ui.gap(col, 16);

        // ----- speed -----
        col.add(Ui.left(Ui.label("Animation speed", Font.BOLD, 14f, Theme.INK_SOFT)));
        Ui.gap(col, 6);
        List<SegmentedControl.Option<Speed>> speedOptions = new ArrayList<>();
        for (Speed s : Speed.values()) {
            speedOptions.add(new SegmentedControl.Option<>(s, s.label()));
        }
        speed = new SegmentedControl<>(speedOptions, controller.speed(), controller::setSpeed);
        col.add(Ui.left(speed));
        Ui.gap(col, 16);

        // ----- grid size -----
        col.add(Ui.left(Ui.label("Grid size", Font.BOLD, 14f, Theme.INK_SOFT)));
        Ui.gap(col, 6);
        List<SegmentedControl.Option<GridSize>> sizeOptions = new ArrayList<>();
        for (GridSize s : GridSize.values()) {
            sizeOptions.add(new SegmentedControl.Option<>(s, s.label()));
        }
        gridSize = new SegmentedControl<>(sizeOptions, controller.size(), controller::changeSize);
        col.add(Ui.left(gridSize));

        refresh();
    }

    private FlatButton action(String text, Runnable onClick) {
        FlatButton button = new FlatButton(text, FlatButton.Variant.SECONDARY);
        button.addActionListener(e -> onClick.run());
        actionButtons.add(button);
        return button;
    }

    /** Called whenever the controller's state changed. */
    public void refresh() {
        boolean running = controller.isRunning();

        algorithm.setValue(controller.mode());
        algorithm.setEnabled(!running);
        caption.setText(controller.mode() == AlgorithmMode.STANDARD
                ? "Textbook A*: scans the whole open list to find the lowest f(n) at every step."
                : "Educational optimization inspired by the research objective: heap priority queue, "
                        + "goal-directed tie-breaking and early exit.");

        findButton.setText(running ? "Stop search" : "Find Path");
        findButton.setVariant(running ? FlatButton.Variant.DANGER : FlatButton.Variant.PRIMARY);

        for (FlatButton b : actionButtons) {
            b.setEnabled(!running);
        }

        speed.setValue(controller.speed()); // speed stays enabled during a search
        gridSize.setValue(controller.size());
        gridSize.setEnabled(!running);
    }
}
