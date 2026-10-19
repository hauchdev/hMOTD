package dev.hauch.hMOTD.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextFormatterTest {

    private final TextFormatter mixedFormatter = new TextFormatter(TextFormatter.Mode.MIXED);
    private final TextFormatter legacyFormatter = new TextFormatter(TextFormatter.Mode.LEGACY);
    private final TextFormatter miniMessageFormatter = new TextFormatter(TextFormatter.Mode.MINIMESSAGE);

    @Test
    void testLegacyColorParsing() {
        Component component = legacyFormatter.format("&aHello &cWorld");
        assertEquals("Hello World", plain(component));
    }

    @Test
    void testMiniMessageParsing() {
        Component component = miniMessageFormatter.format("<red>Hello <blue>World");
        assertEquals("Hello World", plain(component));
        assertEquals(TextColor.color(0xFF5555), component.color());
    }

    @Test
    void testHexColorParsing() {
        Component component = mixedFormatter.format("#00FF00Hello");
        assertEquals("Hello", plain(component));
        assertEquals(TextColor.color(0x00FF00), component.color());
    }

    @Test
    void testMixedFormatDetection() {
        Component miniMessage = mixedFormatter.format("<green>Hello");
        Component legacy = mixedFormatter.format("&aHello");
        assertEquals("Hello", plain(miniMessage));
        assertEquals("Hello", plain(legacy));
    }

    @Test
    void testEmptyInput() {
        Component component = mixedFormatter.format("");
        assertEquals(Component.empty(), component);
    }

    @Test
    void testLegacyHexColor() {
        Component component = legacyFormatter.format("&#FF0000Red");
        assertEquals("Red", plain(component));
        assertEquals(TextColor.color(0xFF0000), component.color());
    }

    @Test
    void testMixedFormatWithAmpersandAndMiniMessage() {
        Component component = mixedFormatter.format("&aHello <red>World");
        assertEquals("Hello World", plain(component));
    }

    private String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
