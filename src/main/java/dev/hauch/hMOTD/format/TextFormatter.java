package dev.hauch.hMOTD.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses text into Adventure Components supporting MiniMessage, legacy ampersand codes,
 * hex colors and a mixed mode that auto-detects the dominant format.
 * Also handles {@code \n} as a line separator.
 */
public final class TextFormatter {

    private static final Pattern HEX_PATTERN = Pattern.compile("(?:&#|#)([0-9a-fA-F]{6})");
    private static final Pattern MINIMESSAGE_TAG_PATTERN = Pattern.compile("<(?:/?[a-zA-Z_#][a-zA-Z0-9_:#\\-]*)>");

    private static final Map<Mode, TextFormatter> INSTANCES = new EnumMap<>(Mode.class);

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
     * Returns a shared formatter for the given mode. Formatters are immutable and therefore safe
     * to share, which avoids re-creating the MiniMessage and legacy serializers on every call.
     */
    public static TextFormatter of(Mode mode) {
        return INSTANCES.computeIfAbsent(mode, TextFormatter::new);
    }

    /**
     * Formats the input into a Component, splitting on {@code \n} to preserve line breaks.
     */
    public Component format(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        List<Component> lines = formatLines(input);
        Component result = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            result = result.append(lines.get(i));
            if (i < lines.size() - 1) {
                result = result.append(Component.newline());
            }
        }
        return result;
    }

    /**
     * Formats the input into one Component per line. The returned list is never empty and is mutable.
     */
    public List<Component> formatLines(String input) {
        List<Component> lines = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            lines.add(Component.empty());
            return lines;
        }

        String[] split = input.split("\\n", -1);
        for (String line : split) {
            lines.add(formatLine(line));
        }
        return lines;
    }

    private Component formatLine(String line) {
        return switch (mode) {
            case MINIMESSAGE -> miniMessage.deserialize(normalizeStandaloneHexForMiniMessage(line));
            case LEGACY -> parseLegacy(line);
            case MIXED -> {
                String processed = normalizeStandaloneHexForMiniMessage(line);
                yield looksLikeMiniMessage(processed) ? miniMessage.deserialize(processed) : parseLegacy(line);
            }
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

    /**
     * Converts standalone hex colors ({@code #RRGGBB} / {@code &#RRGGBB}) outside of MiniMessage tags
     * into MiniMessage hex tags ({@code <#RRGGBB>}).
     */
    private String normalizeStandaloneHexForMiniMessage(String input) {
        StringBuilder result = new StringBuilder(input.length());
        boolean inTag = false;
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);
            if (c == '<') {
                inTag = true;
            } else if (c == '>') {
                inTag = false;
            }

            if (!inTag && (c == '#' || c == '&') && i + 7 <= input.length()) {
                int hexStart = c == '&' ? i + 1 : i;
                if (input.charAt(hexStart) == '#') {
                    String hex = input.substring(hexStart + 1, hexStart + 7);
                    if (hex.matches("[0-9a-fA-F]{6}")) {
                        result.append("<#").append(hex.toUpperCase()).append(">");
                        i = hexStart + 7;
                        continue;
                    }
                }
            }

            result.append(c);
            i++;
        }
        return result.toString();
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
