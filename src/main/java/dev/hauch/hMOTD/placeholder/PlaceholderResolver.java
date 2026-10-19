package dev.hauch.hMOTD.placeholder;

import com.velocitypowered.api.proxy.ProxyServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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

    public PlaceholderResolver(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
        registerDefaults();
    }

    private void registerDefaults() {
        if (proxyServer == null) {
            return;
        }
        dynamicPlaceholders.put("online", () -> String.valueOf(proxyServer.getPlayerCount()));
        dynamicPlaceholders.put("max", () -> String.valueOf(proxyServer.getConfiguration().getShowMaxPlayers()));
        dynamicPlaceholders.put("proxy_name", () -> proxyServer.getConfiguration().getMotd().toString());
        dynamicPlaceholders.put("time", () -> LocalDateTime.now().format(TIME_FORMATTER));
        dynamicPlaceholders.put("date", () -> LocalDateTime.now().format(DATE_FORMATTER));
    }

    public void register(String key, Supplier<String> supplier) {
        staticPlaceholders.put(key.toLowerCase(), supplier);
    }

    public String resolve(String input) {
        return resolve(input, Map.of());
    }

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
