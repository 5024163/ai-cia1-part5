package astar.ui;

import java.awt.Color;
import java.awt.Font;

/** Colours, fonts and small text helpers shared by all screen classes (warehouse-floor palette). */
public final class Theme {
    private Theme() {
    }

    public static final Color INK = new Color(0x172033);
    public static final Color INK_SOFT = new Color(0x364157);
    public static final Color MUTED = new Color(0x5f6b80);
    public static final Color FLOOR = new Color(0xe9edf2);
    public static final Color PANEL = Color.WHITE;
    public static final Color LINE = new Color(0xd3dae4);

    public static final Color CELL = new Color(0xf8fafc);
    public static final Color OBSTACLE = new Color(0x38445a);
    public static final Color ROBOT = new Color(0x2455c7);
    public static final Color ROBOT_DARK = new Color(0x1c46a8);
    public static final Color START_DOCK = new Color(0xd8e1f4);
    public static final Color GOAL = new Color(0x0f8a63);
    public static final Color PATH = new Color(0xf5a300);
    public static final Color EXPANDING = new Color(0xe5484d);
    public static final Color VISITED = new Color(0xc9dcf3);
    public static final Color FRONTIER = new Color(0xe3d8f6);

    /** A soft tint of a colour on a white background (like "bg-goal/10" on the web version). */
    public static Color tint(Color c, double amount) {
        int r = (int) Math.round(255 + (c.getRed() - 255) * amount);
        int g = (int) Math.round(255 + (c.getGreen() - 255) * amount);
        int b = (int) Math.round(255 + (c.getBlue() - 255) * amount);
        return new Color(r, g, b);
    }

    public static Font font(int style, float size) {
        return new Font(Font.SANS_SERIF, style, 12).deriveFont(style, size);
    }
}
