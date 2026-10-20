package dev.hauch.hMOTD.center;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides visual character widths based on Minecraft's default font metrics.
 * Format codes ({@code §} / {@code &} plus one character) are ignored because they have zero visible width.
 */
public final class FontWidthCalculator {

    private static final int DEFAULT_WIDTH = 5;
    private static final Map<Character, Integer> WIDTHS = new HashMap<>();

    static {
        // Whitespace
        WIDTHS.put(' ', 3);

        // Symbols
        WIDTHS.put('!', 1);
        WIDTHS.put('"', 3);
        WIDTHS.put('#', 5);
        WIDTHS.put('$', 5);
        WIDTHS.put('%', 5);
        WIDTHS.put('&', 5);
        WIDTHS.put('\'', 1);
        WIDTHS.put('(', 4);
        WIDTHS.put(')', 4);
        WIDTHS.put('*', 3);
        WIDTHS.put('+', 5);
        WIDTHS.put(',', 1);
        WIDTHS.put('-', 5);
        WIDTHS.put('.', 1);
        WIDTHS.put('/', 5);

        // Digits
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
        for (char c = 'A'; c <= 'Z'; c++) {
            WIDTHS.put(c, c == 'I' ? 3 : 5);
        }

        WIDTHS.put('[', 3);
        WIDTHS.put('\\', 5);
        WIDTHS.put(']', 3);
        WIDTHS.put('^', 5);
        WIDTHS.put('_', 5);
        WIDTHS.put('`', 2);

        // Lowercase letters with accurate widths
        Map<Character, Integer> lower = Map.of(
                'a', 5,
                'b', 5,
                'c', 5,
                'd', 5,
                'e', 5,
                'f', 4,
                'g', 5,
                'h', 5,
                'i', 1,
                'j', 5,
                'k', 4,
                'l', 1,
                'm', 5,
                'n', 5,
                'o', 5,
                'p', 5,
                'q', 5,
                'r', 5,
                's', 5,
                't', 3,
                'u', 5,
                'v', 5,
                'w', 5,
                'x', 5,
                'y', 5,
                'z', 5
        );
        WIDTHS.putAll(lower);

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

    /**
     * Calculates the visible width of a string, ignoring Minecraft format codes.
     */
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
