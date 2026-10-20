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
    void testMiniMessageColorParsing() {
        Component component = miniMessageFormatter.format("<red>Hello <blue>World");
        assertEquals("Hello World", plain(component));
        assertEquals(TextColor.color(0xFF5555), component.children().get(0).color());
    }

    @Test
    void testMiniMessageHexNative() {
        Component component = miniMessageFormatter.format("<#00FF00>Hello");
        assertEquals("Hello", plain(component));
        assertEquals(TextColor.color(0x00FF00), component.children().get(0).color());
    }

    @Test
    void testLegacyHexColorParsing() {
        Component component = legacyFormatter.format("#00FF00Hello");
        assertEquals("Hello", plain(component));
        assertEquals(TextColor.color(0x00FF00), component.children().get(0).color());
    }

    @Test
    void testMixedFormatDetectsMiniMessage() {
        Component component = mixedFormatter.format("<green>Hello");
        assertEquals("Hello", plain(component));
    }

    @Test
    void testMixedFormatDetectsLegacy() {
        Component component = mixedFormatter.format("&aHello");
        assertEquals("Hello", plain(component));
    }

    @Test
    void testLineBreakSupport() {
        Component component = mixedFormatter.format("&aHello\n&bWorld");
        assertEquals("Hello\nWorld", plain(component));
    }

    @Test
    void testStandaloneHexInMiniMessageMode() {
        Component component = miniMessageFormatter.format("#00FF00Hello");
        assertEquals("Hello", plain(component));
        assertEquals(TextColor.color(0x00FF00), component.children().get(0).color());
    }

    @Test
    void testStandaloneHexInMixedModeWithTags() {
        Component component = mixedFormatter.format("<bold>#FF0000Hello</bold>");
        assertEquals("Hello", plain(component));
        assertEquals(TextColor.color(0xFF0000), component.children().get(0).color());
    }

    @Test
    void testMixedModeDoesNotDetectPlainTextAsMiniMessage() {
        Component component = mixedFormatter.format("Hello World");
        assertEquals("Hello World", plain(component));
    }

    @Test
    void testEmptyInput() {
        Component component = mixedFormatter.format("");
        assertEquals(Component.empty(), component);
    }

    private String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
