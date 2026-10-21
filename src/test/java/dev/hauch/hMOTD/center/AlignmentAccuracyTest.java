package dev.hauch.hMOTD.center;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that the produced padding places the text as close as possible to the middle of the
 * server list text area. Because the padding can only be applied in whole spaces the result may
 * differ by at most half a space width.
 */
class AlignmentAccuracyTest {

    private static final int TARGET_WIDTH = 270;
    private static final int SPACE_WIDTH = 4;
    private static final int TOLERANCE = SPACE_WIDTH;

    @ParameterizedTest
    @ValueSource(strings = {"A", "AB", "Hello", "Hello World", "Welcome to hMOTD", "a longer motd line with many characters"})
    void testTextIsCenteredWithinTolerance(String text) {
        Component aligned = TextAligner.align(Component.text(text), TextAligner.Mode.CENTER, TARGET_WIDTH, 0);
        String plain = PlainTextComponentSerializer.plainText().serialize(aligned);

        int spaces = 0;
        while (spaces < plain.length() && plain.charAt(spaces) == ' ') {
            spaces++;
        }
        int padding = spaces * SPACE_WIDTH;
        float textWidth = ComponentWidthCalculator.width(Component.text(text));
        float centre = padding + (textWidth / 2.0F);
        float targetCentre = TARGET_WIDTH / 2.0F;

        assertTrue(Math.abs(centre - targetCentre) <= TOLERANCE,
                "Text '" + text + "' centred at " + centre + " instead of " + targetCentre);
    }

    @Test
    void testPaddingNeverExceedsTarget() {
        Component aligned = TextAligner.align(Component.text("Hello"), TextAligner.Mode.CENTER, TARGET_WIDTH, 0);
        String plain = PlainTextComponentSerializer.plainText().serialize(aligned);
        int spaces = plain.length() - plain.stripLeading().length();
        assertTrue(spaces * SPACE_WIDTH + 24 <= TARGET_WIDTH);
    }
}
