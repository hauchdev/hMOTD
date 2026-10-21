package dev.hauch.hMOTD.placeholder;

import com.velocitypowered.api.proxy.ProxyServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Resolves built-in and user-defined placeholders in text strings.
 */
public final class PlaceholderResolver {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ProxyServer proxyServer;
    private final Map<String, Supplier<String>> staticPlaceholders = new HashMap<>();
    private final Map<String, Supplier<String>> dynamicPlaceholders = new HashMap<>();

    /**
     * Creates a resolver bound to the given proxy.
     *
     * @param proxyServer the running proxy, may be {@code null} in tests
     */
    public PlaceholderResolver(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
        registerDefaults();
    }

    private void registerDefaults() {
        dynamicPlaceholders.put("online", () -> proxyServer == null ? "0" : String.valueOf(proxyServer.getPlayerCount()));
        dynamicPlaceholders.put("max", () -> proxyServer == null ? "0" : String.valueOf(proxyServer.getConfiguration().getShowMaxPlayers()));
        dynamicPlaceholders.put("proxy_name", () -> proxyServer == null ? "" : proxyServer.getConfiguration().getMotd().toString());
        dynamicPlaceholders.put("time", () -> LocalDateTime.now().format(TIME_FORMATTER));
        dynamicPlaceholders.put("date", () -> LocalDateTime.now().format(DATE_FORMATTER));
    }

    /**
     * Checks whether the given text contains a placeholder whose value changes over time.
     * <p>
     * MOTDs using such placeholders must not be cached because the rendered result would become
     * stale.
     *
     * @param input the text to inspect
     * @return whether a live placeholder is present
     */
    public boolean containsDynamicPlaceholder(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        for (String key : dynamicPlaceholders.keySet()) {
            if (input.contains("%" + key + "%")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers an additional static placeholder.
     *
     * @param key      the placeholder name without the percent signs
     * @param supplier the value supplier
     */
    public void register(String key, Supplier<String> supplier) {
        staticPlaceholders.put(key.toLowerCase(Locale.ROOT), supplier);
    }

    /**
     * Resolves the built-in placeholders in the given text.
     *
     * @param input the text to resolve
     * @return the resolved text
     */
    public String resolve(String input) {
        return resolve(input, Map.of());
    }

    /**
     * Resolves the built-in and the given extra placeholders in the given text.
     *
     * @param input the text to resolve
     * @param extra additional static placeholders
     * @return the resolved text
     */
    public String resolve(String input, Map<String, String> extra) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String result = input;
        for (Map.Entry<String, String> entry : extra.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        for (Map.Entry<String, Supplier<String>> entry : staticPlaceholders.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue().get());
        }
        for (Map.Entry<String, Supplier<String>> entry : dynamicPlaceholders.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue().get());
        }
        return result;
    }
}
