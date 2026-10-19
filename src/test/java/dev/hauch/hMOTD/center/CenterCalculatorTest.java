package dev.hauch.hMOTD.center;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CenterCalculatorTest {

    @Test
    void testCenteringAddsSpaces() {
        String centered = CenterCalculator.center("Hello", 248);
        assertTrue(centered.startsWith(" "));
        assertTrue(centered.endsWith("Hello"));
    }

    @Test
    void testCenteringIgnoresFormattingCodes() {
        String centered = CenterCalculator.center("&aHello", 248);
        assertTrue(centered.startsWith(" "));
        assertTrue(centered.endsWith("&aHello"));
    }

    @Test
    void testCenteringIgnoresMiniMessageTags() {
        String centered = CenterCalculator.center("<red>Hello</red>", 248);
        assertTrue(centered.startsWith(" "));
        assertTrue(centered.endsWith("<red>Hello</red>"));
    }

    @Test
    void testEmptyInput() {
        assertEquals("", CenterCalculator.center("", 248));
    }

    @Test
    void testVisibleWidthCalculation() {
        assertEquals(25, CenterCalculator.visibleWidth("Hello")); // H=5, e=5, l=1, l=1, o=5
    }
}
