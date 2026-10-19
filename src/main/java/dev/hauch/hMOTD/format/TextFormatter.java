package dev.hauch.hMOTD.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Component format(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        return switch (mode) {
            case MINIMESSAGE -> parseMiniMessage(input);
            case LEGACY -> parseLegacy(input);
            case MIXED -> parseMixed(input);
        };
    }

    private Component parseMiniMessage(String input) {
        String normalized = normalizeHexForMiniMessage(input);
        return miniMessage.deserialize(normalized);
    }

    private Component parseLegacy(String input) {
        String normalized = normalizeHexForLegacy(input);
        normalized = normalized.replace('&', '§');
        return legacySerializer.deserialize(normalized);
    }

    private Component parseMixed(String input) {
        if (looksLikeMiniMessage(input)) {
            return parseMiniMessage(input);
        }
        return parseLegacy(input);
    }

    private boolean looksLikeMiniMessage(String input) {
        if (MINIMESSAGE_TAG_PATTERN.matcher(input).find()) {
            return true;
        }
        String normalized = normalizeHexForMiniMessage(input);
        return MINIMESSAGE_TAG_PATTERN.matcher(normalized).find();
    }

    private String normalizeHexForMiniMessage(String input) {
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1).toUpperCase() + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
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

    public enum Mode {
        MINIMESSAGE,
        LEGACY,
        MIXED
    }
}
