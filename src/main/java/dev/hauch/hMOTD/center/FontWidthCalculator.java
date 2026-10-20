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
        WIDTHS.put('a', 5);
        WIDTHS.put('b', 5);
        WIDTHS.put('c', 5);
        WIDTHS.put('d', 5);
        WIDTHS.put('e', 5);
        WIDTHS.put('f', 4);
        WIDTHS.put('g', 5);
        WIDTHS.put('h', 5);
        WIDTHS.put('i', 1);
        WIDTHS.put('j', 5);
        WIDTHS.put('k', 4);
        WIDTHS.put('l', 1);
        WIDTHS.put('m', 5);
        WIDTHS.put('n', 5);
        WIDTHS.put('o', 5);
        WIDTHS.put('p', 5);
        WIDTHS.put('q', 5);
        WIDTHS.put('r', 5);
        WIDTHS.put('s', 5);
        WIDTHS.put('t', 3);
        WIDTHS.put('u', 5);
        WIDTHS.put('v', 5);
        WIDTHS.put('w', 5);
        WIDTHS.put('x', 5);
        WIDTHS.put('y', 5);
        WIDTHS.put('z', 5);

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
