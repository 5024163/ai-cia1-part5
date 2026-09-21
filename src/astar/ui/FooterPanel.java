package astar.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;

/** Reference line at the bottom of the window. */
public class FooterPanel extends JPanel {
    public FooterPanel(int contentWidth) {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.LINE));

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(22, 0, 22, 0));
        inner.setPreferredSize(new Dimension(contentWidth, 66));

        JLabel label = new JLabel("Based on:  \u201COptimizing the A* Search Algorithm for Mobile Robotic Devices\u201D");
        label.setFont(Theme.font(Font.PLAIN, 14f));
        label.setForeground(Theme.MUTED);
        inner.add(label);
        add(inner);
    }
}
