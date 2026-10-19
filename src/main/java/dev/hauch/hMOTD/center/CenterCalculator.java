package dev.hauch.hMOTD.center;

import java.util.regex.Pattern;

public final class CenterCalculator {

    private static final Pattern MINIMESSAGE_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern FORMAT_CODE_PATTERN = Pattern.compile("[§&][0-9a-fk-orA-FK-OR]");
    private static final int SPACE_WIDTH = 3;

    private CenterCalculator() {
    }

    public static String center(String text, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        int visibleWidth = visibleWidth(text);
        if (visibleWidth >= maxWidth) {
            return text;
        }
        int remaining = maxWidth - visibleWidth;
        int spaces = remaining / SPACE_WIDTH;
        return " ".repeat(spaces) + text;
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
