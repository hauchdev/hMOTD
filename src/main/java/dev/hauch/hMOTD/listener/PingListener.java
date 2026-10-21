package dev.hauch.hMOTD.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import dev.hauch.hMOTD.cache.IconManager;
import dev.hauch.hMOTD.config.hMOTDConfig;
import dev.hauch.hMOTD.format.TextFormatter;
import dev.hauch.hMOTD.motd.MOTDManager;
import dev.hauch.hMOTD.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Listens to proxy ping events and customizes the server list response.
 */
public final class PingListener {

    private final AtomicReference<hMOTDConfig> config;
    private final MOTDManager motdManager;
    private final IconManager iconManager;
    private final PlaceholderResolver placeholderResolver;
    private final Logger logger;

    /**
     * Creates the proxy ping listener.
     *
     * @param config              the live configuration reference
     * @param motdManager         the MOTD manager
     * @param iconManager         the icon manager
     * @param placeholderResolver the placeholder resolver
     * @param logger              the plugin logger
     */
    public PingListener(AtomicReference<hMOTDConfig> config, MOTDManager motdManager, IconManager iconManager, PlaceholderResolver placeholderResolver, Logger logger) {
        this.config = config;
        this.motdManager = motdManager;
        this.iconManager = iconManager;
        this.placeholderResolver = placeholderResolver;
        this.logger = logger;
    }

    private TextFormatter formatter(hMOTDConfig cfg) {
        return TextFormatter.of(TextFormatter.Mode.valueOf(cfg.formatMode().name()));
    }

    /**
     * Customises the server list response of every proxy ping.
     *
     * @param event the ping event
     */
    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        try {
            hMOTDConfig cfg = config.get();
            ServerPing original = event.getPing();
            ServerPing.Builder builder = original.asBuilder();

            builder.description(motdManager.nextMOTD());

            applyIcon(builder, cfg);
            applyPlayers(builder, original, cfg);
            applyVersion(builder, original, cfg);

            event.setPing(builder.build());
        } catch (Exception e) {
            // Never break the proxy ping response; fall back to the original ping.
            if (logger != null) {
                logger.warn("Failed to handle proxy ping", e);
            }
        }
    }

    private void applyIcon(ServerPing.Builder builder, hMOTDConfig cfg) {
        Path icon = iconManager.nextIcon(cfg.icon());
        if (icon != null) {
            try {
                builder.favicon(Favicon.create(icon));
            } catch (Exception e) {
                // ignore invalid icon
            }
        }
    }

    private void applyPlayers(ServerPing.Builder builder, ServerPing original, hMOTDConfig cfg) {
        hMOTDConfig.ServerInfoConfig info = cfg.serverInfo();
        Optional<ServerPing.Players> originalPlayers = original.getPlayers();

        int online = originalPlayers.map(ServerPing.Players::getOnline).orElse(0);
        int max = originalPlayers.map(ServerPing.Players::getMax).orElse(0);

        if (info.overridePlayers()) {
            online = info.onlinePlayers();
            max = info.maxPlayers();
        }

        if (info.hideOnline() && info.hideMax()) {
            builder.nullPlayers();
            return;
        }

        if (info.hideOnline()) {
            online = -1;
        }
        if (info.hideMax()) {
            max = -1;
        }

        List<ServerPing.SamplePlayer> sample = new ArrayList<>();
        if (info.overrideSample() && info.sampleLines() != null && !info.sampleLines().isEmpty()) {
            TextFormatter formatter = formatter(cfg);
            for (String line : info.sampleLines()) {
                String resolved = resolvePlaceholders(line, cfg);
                Component component = formatter.format(resolved);
                sample.add(new ServerPing.SamplePlayer(formatter.serializeLegacy(component), java.util.UUID.randomUUID()));
            }
        } else {
            originalPlayers.ifPresent(players -> sample.addAll(players.getSample()));
        }

        builder.samplePlayers(sample);
        if (online != -1 && max != -1) {
            builder.onlinePlayers(online);
            builder.maximumPlayers(max);
        }
    }

    private void applyVersion(ServerPing.Builder builder, ServerPing original, hMOTDConfig cfg) {
        hMOTDConfig.ServerInfoConfig info = cfg.serverInfo();
        if (!info.overrideVersion()) {
            return;
        }
        int protocol = Optional.ofNullable(original.getVersion())
                .map(ServerPing.Version::getProtocol)
                .orElse(0);
        TextFormatter formatter = formatter(cfg);
        String resolvedVersion = resolvePlaceholders(info.versionText(), cfg);
        Component versionComponent = formatter.format(resolvedVersion);
        builder.version(new ServerPing.Version(protocol, formatter.serializeLegacy(versionComponent)));
    }

    private String resolvePlaceholders(String input, hMOTDConfig cfg) {
        if (!cfg.placeholders()) {
            return input;
        }
        return placeholderResolver.resolve(input, cfg.extraPlaceholders());
    }
}
