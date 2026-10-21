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
        // width of "Hello" is 26, so (270 - 26) / 2 - 1 = 121 -> 30 spaces
        assertEquals(30, leadingSpaces(plain));
    }

    @Test
    void testCenterAlignmentWithBold() {
        Component bold = Component.text("Hello").decorate(TextDecoration.BOLD);
        Component aligned = TextAligner.align(bold, TextAligner.Mode.CENTER, TARGET_WIDTH, 0);
        String plain = plain(aligned);
        // bold width of "Hello" is 31, so (270 - 31) / 2 - 1 = 118 -> 29 spaces
        assertEquals(29, leadingSpaces(plain));
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
        assertEquals(31, leadingSpaces(plain(aligned)));
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
