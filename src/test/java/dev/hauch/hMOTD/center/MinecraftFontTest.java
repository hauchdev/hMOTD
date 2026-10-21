package dev.hauch.hMOTD.center;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinecraftFontTest {

    @Test
    void testSpaceAdvance() {
        assertEquals(4.0F, MinecraftFont.advance(' ', false));
        assertEquals(5.0F, MinecraftFont.advance(' ', true));
    }

    @Test
    void testLetterAdvances() {
        assertEquals(6.0F, MinecraftFont.advance('A', false));
        assertEquals(6.0F, MinecraftFont.advance('H', false));
        assertEquals(6.0F, MinecraftFont.advance('e', false));
        assertEquals(3.0F, MinecraftFont.advance('l', false));
        assertEquals(2.0F, MinecraftFont.advance('i', false));
        assertEquals(4.0F, MinecraftFont.advance('t', false));
    }

    @Test
    void testSymbolAdvances() {
        assertEquals(2.0F, MinecraftFont.advance('!', false));
        assertEquals(4.0F, MinecraftFont.advance('"', false));
        assertEquals(6.0F, MinecraftFont.advance('#', false));
        assertEquals(4.0F, MinecraftFont.advance('(', false));
        assertEquals(7.0F, MinecraftFont.advance('@', false));
    }

    @Test
    void testBoldIsWider() {
        assertEquals(MinecraftFont.advance('A', false) + 1.0F, MinecraftFont.advance('A', true));
        assertEquals(MinecraftFont.advance('l', false) + 1.0F, MinecraftFont.advance('l', true));
    }

    @Test
    void testUnknownCodePointFallsBack() {
        assertEquals(MinecraftFont.DEFAULT_ADVANCE, MinecraftFont.advance(0x1F600, false));
        assertEquals(MinecraftFont.DEFAULT_ADVANCE_BOLD, MinecraftFont.advance(0x1F600, true));
        assertEquals(MinecraftFont.DEFAULT_ADVANCE, MinecraftFont.advance(-1, false));
    }
}
