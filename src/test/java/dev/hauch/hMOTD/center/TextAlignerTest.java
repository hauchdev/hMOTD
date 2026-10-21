package dev.hauch.hMOTD.center;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextAlignerTest {

    private static final int TARGET_WIDTH = 270;

    @Test
    void testCenterAlignment() {
        Component aligned = TextAligner.align(Component.text("Hello"), TextAligner.Mode.CENTER, TARGET_WIDTH, 0);
        String plain = plain(aligned);
        assertTrue(plain.endsWith("Hello"));
        // width of "Hello" is 24, so the ideal padding is 123 -> 31 spaces (124px) is the closest
        assertEquals(31, leadingSpaces(plain));
    }

    @Test
    void testCenterAlignmentWithBold() {
        Component bold = Component.text("Hello").decorate(TextDecoration.BOLD);
        Component aligned = TextAligner.align(bold, TextAligner.Mode.CENTER, TARGET_WIDTH, 0);
        String plain = plain(aligned);
        // bold width of "Hello" is 29, so the ideal padding is 120.5 -> 30 spaces (120px)
        assertEquals(30, leadingSpaces(plain));
    }

    @Test
    void testLeftAlignmentKeepsText() {
        Component aligned = TextAligner.align(Component.text("Hello"), TextAligner.Mode.LEFT, TARGET_WIDTH, 0);
        assertEquals("Hello", plain(aligned));
    }

    @Test
    void testRightAlignment() {
        Component aligned = TextAligner.align(Component.text("Hello"), TextAligner.Mode.RIGHT, TARGET_WIDTH, 0);
        String plain = plain(aligned);
        assertTrue(plain.endsWith("Hello"));
        // 270 - 26 = 244 -> 61 spaces
        assertEquals(61, leadingSpaces(plain));
    }

    @Test
    void testOffsetShiftsText() {
        Component aligned = TextAligner.align(Component.text("Hello"), TextAligner.Mode.CENTER, TARGET_WIDTH, 4);
        // ideal padding becomes 127 -> 32 spaces (128px) is the closest
        assertEquals(32, leadingSpaces(plain(aligned)));
    }

    @Test
    void testTextWiderThanTargetIsNotPadded() {
        Component longText = Component.text("a".repeat(100));
        Component aligned = TextAligner.align(longText, TextAligner.Mode.CENTER, TARGET_WIDTH, 0);
        assertEquals(0, leadingSpaces(plain(aligned)));
    }

    private int leadingSpaces(String text) {
        int count = 0;
        while (count < text.length() && text.charAt(count) == ' ') {
            count++;
        }
        return count;
    }

    private String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
