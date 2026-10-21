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
        int paddingPixels = switch (mode) {
            case CENTER -> (int) Math.max(0.0F, Math.floor((targetWidth - width) / 2.0F) - 1.0F);
            case RIGHT -> (int) Math.max(0.0F, Math.floor(targetWidth - width));
            case LEFT -> 0;
        };

        int spaces = Math.max(0, Math.floorDiv(paddingPixels + offset, SPACE_WIDTH));
        if (spaces == 0) {
            return line;
        }
        return Component.text(" ".repeat(spaces)).append(line);
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
