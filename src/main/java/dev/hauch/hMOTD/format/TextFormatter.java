package dev.hauch.hMOTD.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses text into Adventure Components supporting MiniMessage, legacy ampersand codes,
 * hex colors and a mixed mode that auto-detects the dominant format.
 * Also handles {@code \n} as a line separator.
 */
public final class TextFormatter {

    private static final Pattern HEX_PATTERN = Pattern.compile("(?:&#|#)([0-9a-fA-F]{6})");
    private static final Pattern MINIMESSAGE_TAG_PATTERN = Pattern.compile("<[^>]+>");

    private final Mode mode;
    private final MiniMessage miniMessage;
    private final LegacyComponentSerializer legacySerializer;

    public TextFormatter(Mode mode) {
        this.mode = mode;
        this.miniMessage = MiniMessage.miniMessage();
        this.legacySerializer = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
    }

    /**
     * Formats the input into a Component, splitting on {@code \n} to preserve line breaks.
     */
    public Component format(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        String[] lines = input.split("\\n", -1);
        Component result = Component.empty();
        for (int i = 0; i < lines.length; i++) {
            result = result.append(formatLine(lines[i]));
            if (i < lines.length - 1) {
                result = result.append(Component.newline());
            }
        }
        return result;
    }

    private Component formatLine(String line) {
        return switch (mode) {
            case MINIMESSAGE -> miniMessage.deserialize(line);
            case LEGACY -> parseLegacy(line);
            case MIXED -> looksLikeMiniMessage(line) ? miniMessage.deserialize(line) : parseLegacy(line);
        };
    }

    private Component parseLegacy(String input) {
        String normalized = normalizeHexForLegacy(input);
        normalized = normalized.replace('&', '§');
        return legacySerializer.deserialize(normalized);
    }

    private boolean looksLikeMiniMessage(String input) {
        return input.indexOf('<') >= 0 && MINIMESSAGE_TAG_PATTERN.matcher(input).find();
    }

    private String normalizeHexForLegacy(String input) {
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1).toLowerCase();
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String serializeLegacy(Component component) {
        return legacySerializer.serialize(component);
    }

    public String serializePlain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public enum Mode {
        MINIMESSAGE,
        LEGACY,
        MIXED
    }
}
