package dev.hauch.hMOTD.placeholder;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
