package dev.hauch.hMOTD.center;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentWidthCalculatorTest {

    @Test
    void testPlainTextWidth() {
        // H=6, e=6, l=3, l=3, o=6
        assertEquals(24.0F, ComponentWidthCalculator.width(Component.text("Hello")));
    }

    @Test
    void testBoldTextIsWider() {
        // H=7, e=7, l=4, l=4, o=7
        assertEquals(29.0F, ComponentWidthCalculator.width(
                Component.text("Hello").decorate(TextDecoration.BOLD)));
    }

    @Test
    void testEmptyComponent() {
        assertEquals(0.0F, ComponentWidthCalculator.width(Component.empty()));
    }

    @Test
    void testSpaceIsCounted() {
        assertEquals(4.0F, ComponentWidthCalculator.width(Component.text(" ")));
    }
}
