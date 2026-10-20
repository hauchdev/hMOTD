package dev.hauch.hMOTD.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
        assertEquals(248, config.center().width());
        assertEquals(4, config.center().spaceWidth());
    }

    @Test
    void testValidateCorrectsInvalidWidth() throws Exception {
        ConfigLoader loader = new ConfigLoader(tempDir);
        hMOTDConfig config = loader.load();
        config.center().width(-10);
        config.validate();

        assertEquals(248, config.center().width());
    }
}
