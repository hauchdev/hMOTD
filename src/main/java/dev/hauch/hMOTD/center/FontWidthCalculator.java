package dev.hauch.hMOTD.center;

import java.util.HashMap;
import java.util.Map;

public final class FontWidthCalculator {

    private static final int DEFAULT_WIDTH = 5;
    private static final Map<Character, Integer> WIDTHS = new HashMap<>();

    static {
        // Space
        WIDTHS.put(' ', 3);

        // Symbols
        WIDTHS.put('!', 1);
        WIDTHS.put('"', 3);
        WIDTHS.put('#', 5);
        WIDTHS.put('$', 5);
        WIDTHS.put('%', 5);
        WIDTHS.put('&', 5);
        WIDTHS.put('\'', 1);
        WIDTHS.put('(', 3);
        WIDTHS.put(')', 3);
        WIDTHS.put('*', 3);
        WIDTHS.put('+', 5);
        WIDTHS.put(',', 1);
        WIDTHS.put('-', 5);
        WIDTHS.put('.', 1);
        WIDTHS.put('/', 5);

        // Numbers
        for (char c = '0'; c <= '9'; c++) {
            WIDTHS.put(c, 5);
        }

        // More symbols
        WIDTHS.put(':', 1);
        WIDTHS.put(';', 1);
        WIDTHS.put('<', 4);
        WIDTHS.put('=', 5);
        WIDTHS.put('>', 4);
        WIDTHS.put('?', 5);
        WIDTHS.put('@', 6);

        // Uppercase letters
        String upperNarrow = "I";
        for (char c = 'A'; c <= 'Z'; c++) {
            WIDTHS.put(c, upperNarrow.indexOf(c) >= 0 ? 3 : 5);
        }

        WIDTHS.put('[', 3);
        WIDTHS.put('\\', 5);
        WIDTHS.put(']', 3);
        WIDTHS.put('^', 5);
        WIDTHS.put('_', 5);
        WIDTHS.put('`', 2);

        // Lowercase letters
        Map<Character, Integer> lowerWidths = Map.of(
                'f', 4,
                'i', 1,
                'j', 5,
                'k', 4,
                'l', 1,
                't', 3
        );
        for (char c = 'a'; c <= 'z'; c++) {
            WIDTHS.put(c, lowerWidths.getOrDefault(c, 5));
        }

        WIDTHS.put('{', 3);
        WIDTHS.put('|', 1);
        WIDTHS.put('}', 3);
        WIDTHS.put('~', 6);
    }

    private FontWidthCalculator() {
    }

    public static int widthOf(char c) {
        return WIDTHS.getOrDefault(c, DEFAULT_WIDTH);
    }

    public static int widthOf(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int width = 0;
        boolean skipNext = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '§' || c == '&') {
                skipNext = true;
                continue;
            }
            if (skipNext) {
                skipNext = false;
                continue;
            }
            width += widthOf(c);
        }
        return width;
    }
}
