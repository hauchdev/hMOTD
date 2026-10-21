package dev.hauch.hMOTD.center;

import net.kyori.adventure.text.Component;

/**
 * Aligns a rendered MOTD line inside the server list text area.
 * <p>
 * The Minecraft server list renders the MOTD left aligned inside a fixed area, therefore exact
 * centering has to be emulated by prefixing the line with space characters. The amount of spaces
 * is derived from the real rendered width of the line (see {@link ComponentWidthCalculator}) and the
 * real advance of a space character.
 */
public final class TextAligner {

    private static final int SPACE_WIDTH = 4;

    private TextAligner() {
    }

    /**
     * Aligns a single line.
     *
     * @param line        the rendered line
     * @param mode        the alignment mode
     * @param targetWidth the width of the server list text area in pixels
     * @param offset      a fine tuning offset in pixels
     * @return the aligned line
     */
    public static Component align(Component line, Mode mode, int targetWidth, int offset) {
        if (mode == Mode.LEFT || targetWidth <= 0) {
            return line;
        }

        float width = ComponentWidthCalculator.width(line);
        int spaces = switch (mode) {
            case CENTER -> spacesForCenter(targetWidth, width, offset);
            case RIGHT -> spacesForRight(targetWidth, width, offset);
            case LEFT -> 0;
        };

        if (spaces <= 0) {
            return line;
        }
        return Component.text(" ".repeat(spaces)).append(line);
    }

    /**
     * Calculates the amount of spaces that places the line as close as possible to the middle of
     * the text area. Because the padding can only be applied in whole spaces the result may still be
     * off by up to half a space width.
     */
    private static int spacesForCenter(int targetWidth, float width, int offset) {
        double idealPadding = (targetWidth - width) / 2.0D + offset;
        if (idealPadding <= 0.0D) {
            return 0;
        }
        int lower = (int) Math.floor(idealPadding / SPACE_WIDTH);
        int upper = lower + 1;
        double lowerError = Math.abs((lower * SPACE_WIDTH) - idealPadding);
        double upperError = Math.abs((upper * SPACE_WIDTH) - idealPadding);
        return lowerError <= upperError ? lower : upper;
    }

    private static int spacesForRight(int targetWidth, float width, int offset) {
        double idealPadding = targetWidth - width + offset;
        if (idealPadding <= 0.0D) {
            return 0;
        }
        int lower = (int) Math.floor(idealPadding / SPACE_WIDTH);
        int upper = lower + 1;
        double lowerError = Math.abs((lower * SPACE_WIDTH) - idealPadding);
        double upperError = Math.abs((upper * SPACE_WIDTH) - idealPadding);
        return lowerError <= upperError ? lower : upper;
    }

    /**
     * Alignment modes supported by the plugin.
     */
    public enum Mode {
        /**
         * Renders the line at the left edge of the text area (vanilla behaviour).
         */
        LEFT,
        /**
         * Centers the line inside the text area.
         */
        CENTER,
        /**
         * Renders the line at the right edge of the text area.
         */
        RIGHT
    }
}
