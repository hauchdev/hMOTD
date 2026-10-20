package dev.hauch.hMOTD.motd;

import dev.hauch.hMOTD.config.hMOTDConfig;
import dev.hauch.hMOTD.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        config.center().width(21);
        config.center().spaceWidth(4);

        MOTDManager manager = new MOTDManager(logger, resolver, config);
        String result = plain(manager.nextMOTD());
        assertEquals("A", result.stripLeading());
        assertTrue(result.startsWith("  ")); // 2 spaces of padding
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
