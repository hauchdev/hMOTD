package dev.hauch.hMOTD.center;

import java.util.regex.Pattern;

/**
 * Centers MOTD lines based on their visible Minecraft font width.
 * Supports multi-line input split by {@code \n}.
 */
public final class CenterCalculator {

    private static final Pattern MINIMESSAGE_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern FORMAT_CODE_PATTERN = Pattern.compile("[§&][0-9a-fk-orA-FK-OR]");

    private CenterCalculator() {
    }

    /**
     * Centers each line of the input separately using the given maximum width and space width.
     */
    public static String center(String text, int maxWidth, int spaceWidth) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String[] lines = text.split("\\n", -1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            result.append(centerLine(lines[i], maxWidth, spaceWidth));
            if (i < lines.length - 1) {
                result.append('\n');
            }
        }
        return result.toString();
    }

    private static String centerLine(String line, int maxWidth, int spaceWidth) {
        if (line == null || line.isEmpty()) {
            return line;
        }
        int visibleWidth = visibleWidth(line);
        if (visibleWidth >= maxWidth) {
            return line;
        }
        int remaining = maxWidth - visibleWidth;
        int spaces = remaining / (2 * spaceWidth);
        return " ".repeat(spaces) + line;
    }

    public static int visibleWidth(String text) {
        String stripped = stripFormatting(text);
        return FontWidthCalculator.widthOf(stripped);
    }

    private static String stripFormatting(String text) {
        String withoutTags = MINIMESSAGE_TAG_PATTERN.matcher(text).replaceAll("");
        return FORMAT_CODE_PATTERN.matcher(withoutTags).replaceAll("");
    }
}
