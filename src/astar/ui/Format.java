package astar.ui;

import java.util.Locale;

final class Format {
    private Format() {
    }

    /** Formats milliseconds for display, e.g. "0.042 ms" or "1.35 ms". */
    static String time(double ms) {
        if (ms < 0.001) return "<0.001 ms";
        if (ms < 1) return String.format(Locale.ROOT, "%.3f ms", ms);
        return String.format(Locale.ROOT, "%.2f ms", ms);
    }

    static String percent(double value) {
        return String.format(Locale.ROOT, "%.1f%%", value);
    }
}
