package dev.hauch.hMOTD.center;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CenterCalculatorTest {

    @Test
    void testCenteringAddsSpaces() {
        String centered = CenterCalculator.center("Hello", 248, 4);
        assertTrue(centered.startsWith(" "));
        assertTrue(centered.endsWith("Hello"));
    }

    @Test
    void testCenteringIgnoresFormattingCodes() {
        String centered = CenterCalculator.center("&aHello", 248, 4);
        assertTrue(centered.startsWith(" "));
        assertTrue(centered.endsWith("&aHello"));
    }

    @Test
    void testCenteringIgnoresMiniMessageTags() {
        String centered = CenterCalculator.center("<red>Hello</red>", 248, 4);
        assertTrue(centered.startsWith(" "));
        assertTrue(centered.endsWith("<red>Hello</red>"));
    }

    @Test
    void testEmptyInput() {
        assertEquals("", CenterCalculator.center("", 248, 4));
    }

    @Test
    void testVisibleWidthCalculation() {
        assertEquals(17, CenterCalculator.visibleWidth("Hello")); // H=5, e=5, l=1, l=1, o=5
    }

    @Test
    void testMultiLineCentering() {
        String centered = CenterCalculator.center("Hello\nWorld", 248, 4);
        String[] lines = centered.split("\n");
        assertEquals(2, lines.length);
        assertTrue(lines[0].endsWith("Hello"));
        assertTrue(lines[1].endsWith("World"));
    }

    @Test
    void testCenteringCalculation() {
        // "A" width = 5, maxWidth = 21, spaceWidth = 4
        // remaining = 16, spaces = 16 / (2 * 4) = 2
        String centered = CenterCalculator.center("A", 21, 4);
        assertEquals("  A", centered);
    }
}
