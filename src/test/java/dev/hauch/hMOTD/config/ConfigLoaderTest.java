package dev.hauch.hMOTD.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testLoadCreatesDefaultConfig() throws Exception {
        ConfigLoader loader = new ConfigLoader(tempDir);
        hMOTDConfig config = loader.load();

        assertNotNull(config);
        assertFalse(config.motds().isEmpty());
        assertTrue(config.center().enabled());
        assertEquals(270, config.center().width());
        assertEquals(hMOTDConfig.AlignMode.CENTER, config.center().mode());
        assertEquals(0, config.center().offset());
    }

    @Test
    void testValidateCorrectsInvalidWidth() throws Exception {
        ConfigLoader loader = new ConfigLoader(tempDir);
        hMOTDConfig config = loader.load();
        config.center().width(-10);
        config.validate();

        assertEquals(270, config.center().width());
    }

    @Test
    void testLegacyConfigIsMigrated() throws Exception {
        Files.writeString(tempDir.resolve("config.yml"), """
                motds:
                  - "Legacy"
                center:
                  enabled: true
                  width: 248
                  space-width: 4
                """);

        ConfigLoader loader = new ConfigLoader(tempDir);
        hMOTDConfig config = loader.load();

        assertEquals(270, config.center().width());

        String migrated = Files.readString(tempDir.resolve("config.yml"));
        assertFalse(migrated.contains("space-width"), "obsolete option was not removed");
        assertTrue(migrated.contains("270"), "default width was not updated");
    }
}
