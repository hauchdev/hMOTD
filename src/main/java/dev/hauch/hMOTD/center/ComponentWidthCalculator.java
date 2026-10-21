package dev.hauch.hMOTD.center;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Measures the rendered width of a Component using Minecraft's real font metrics.
 * <p>
 * Bold text is measured with the bold advances because the client renders bold glyphs wider.
 */
public final class ComponentWidthCalculator {

    private static final ComponentFlattener FLATTENER = ComponentFlattener.textOnly();

    private ComponentWidthCalculator() {
    }

    /**
     * Calculates the rendered width of the given component in pixels.
     *
     * @param component the component to measure
     * @return the rendered width in pixels
     */
    public static float width(Component component) {
        WidthListener listener = new WidthListener();
        FLATTENER.flatten(component, listener);
        return listener.width;
    }

    private static final class WidthListener implements FlattenerListener {

        private float width;
        private boolean bold;

        @Override
        public void pushStyle(Style style) {
            if (style.hasDecoration(TextDecoration.BOLD)) {
                bold = true;
            }
        }

        @Override
        public void component(String text) {
            int length = text.length();
            for (int i = 0; i < length; ) {
                int codePoint = text.codePointAt(i);
                width += MinecraftFont.advance(codePoint, bold);
                i += Character.charCount(codePoint);
            }
        }

        @Override
        public void popStyle(Style style) {
            if (style.hasDecoration(TextDecoration.BOLD)) {
                bold = false;
            }
        }
    }
}
