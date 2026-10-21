package dev.hauch.hMOTD.motd;

import dev.hauch.hMOTD.config.hMOTDConfig;
import dev.hauch.hMOTD.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MOTDManagerTest {

    private final Logger logger = LoggerFactory.getLogger(MOTDManagerTest.class);
    private final PlaceholderResolver resolver = new PlaceholderResolver(null);

    @Test
    void testSingleMotd() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("&aHello World");
        config.center().enabled(false);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        assertEquals("Hello World", plain(manager.nextMOTD()));
    }

    @Test
    void testPlaceholderReplacement() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("Hello %name%");
        config.extraPlaceholders().put("name", "hMOTD");
        config.center().enabled(false);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        assertEquals("Hello hMOTD", plain(manager.nextMOTD()));
    }

    @Test
    void testCentering() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("A");
        config.center().enabled(true);
        config.center().width(270);
        config.center().mode(hMOTDConfig.AlignMode.CENTER);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        String result = plain(manager.nextMOTD());
        assertEquals("A", result.stripLeading());
        // width of "A" is 6, so the ideal padding is 132 -> 33 spaces (132px)
        assertTrue(result.startsWith(" ".repeat(33)));
    }

    @Test
    void testMultiLineCentering() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("A\nBB");
        config.center().enabled(true);
        config.center().width(270);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        String[] lines = plain(manager.nextMOTD()).split("\n");
        assertEquals(2, lines.length);
        // "A" is 6px wide -> 33 spaces, "BB" is 12px wide -> 32 spaces
        assertTrue(lines[0].startsWith(" ".repeat(33)));
        assertTrue(lines[1].startsWith(" ".repeat(32)));
    }

    @Test
    void testEmptyMotdDoesNotFail() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("");
        config.center().enabled(true);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        assertEquals("", plain(manager.nextMOTD()));
    }

    @Test
    void testLeftAlignmentDoesNotPad() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("Hello");
        config.center().enabled(true);
        config.center().mode(hMOTDConfig.AlignMode.LEFT);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        assertEquals("Hello", plain(manager.nextMOTD()));
    }

    @Test
    void testRightAlignmentPadsToTheRight() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("Hello");
        config.center().enabled(true);
        config.center().mode(hMOTDConfig.AlignMode.RIGHT);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        String result = plain(manager.nextMOTD());
        assertTrue(result.endsWith("Hello"));
        // 270 - 24 = 246 -> 61 spaces
        assertTrue(result.startsWith(" ".repeat(61)));
    }

    @Test
    void testStaticMotdIsCached() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("Static MOTD");
        config.center().enabled(false);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        assertSame(manager.nextMOTD(), manager.nextMOTD());
    }

    @Test
    void testDynamicMotdIsNotCached() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("Players: %online%");
        config.center().enabled(false);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        assertNotSame(manager.nextMOTD(), manager.nextMOTD());
    }

    @Test
    void testMultiLineMotd() {
        hMOTDConfig config = new hMOTDConfig();
        config.motds().clear();
        config.motds().add("&aLine1\n&bLine2");
        config.center().enabled(false);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        assertEquals("Line1\nLine2", plain(manager.nextMOTD()));
    }

    private String plain(net.kyori.adventure.text.Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
