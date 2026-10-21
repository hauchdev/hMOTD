package dev.hauch.hMOTD.placeholder;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceholderResolverTest {

    @Test
    void testCustomExtraPlaceholders() {
        PlaceholderResolver resolver = new PlaceholderResolver(null);
        String result = resolver.resolve("Welcome to %server_name%", Map.of("server_name", "hMOTD Network"));
        assertEquals("Welcome to hMOTD Network", result);
    }

    @Test
    void testMultipleExtraPlaceholders() {
        PlaceholderResolver resolver = new PlaceholderResolver(null);
        String result = resolver.resolve("%a% and %b%", Map.of("a", "Hello", "b", "World"));
        assertEquals("Hello and World", result);
    }

    @Test
    void testEmptyInput() {
        PlaceholderResolver resolver = new PlaceholderResolver(null);
        assertEquals("", resolver.resolve("", Map.of()));
    }

    @Test
    void testDynamicPlaceholdersAreDetected() {
        PlaceholderResolver resolver = new PlaceholderResolver(null);
        assertTrue(resolver.containsDynamicPlaceholder("Players: %online%"));
        assertTrue(resolver.containsDynamicPlaceholder("Time: %time%"));
        assertFalse(resolver.containsDynamicPlaceholder("Static %name% text"));
        assertFalse(resolver.containsDynamicPlaceholder(""));
    }

    @Test
    void testBuiltInPlaceholdersResolveWithoutProxy() {
        PlaceholderResolver resolver = new PlaceholderResolver(null);
        assertEquals("0 players", resolver.resolve("%online% players", Map.of()));
    }
}
