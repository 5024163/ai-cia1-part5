package astar.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;

/** Short "how to use" card shown under the control panel. */
public class InstructionsPanel extends CardPanel {
    public InstructionsPanel(int width) {
        super(new BorderLayout(), width, 20, 20);
        JPanel col = Ui.column();
        add(col, BorderLayout.CENTER);

        col.add(Ui.left(Ui.label("How to use the grid", Font.BOLD, 20f, Theme.INK)));
        Ui.gap(col, 10);
        String[] lines = {
                "Click or drag on a cell to add or remove an obstacle.",
                "Use the Set Start tool to place the robot. Click the robot again to remove it.",
                "Use the Set Goal tool to place the destination.",
                "Press Find Path to run A* and watch the search.",
                "Switch algorithm and run again on the same map to compare."
        };
        for (String line : lines) {
            col.add(Ui.left(Ui.wrapped(width - 40, "\u2022  " + line, Font.PLAIN, 14f, Theme.INK_SOFT)));
            Ui.gap(col, 5);
        }
    }
}
