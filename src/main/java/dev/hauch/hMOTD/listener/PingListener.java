package dev.hauch.hMOTD.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import dev.hauch.hMOTD.cache.IconCache;
import dev.hauch.hMOTD.config.hMOTDConfig;
import dev.hauch.hMOTD.format.TextFormatter;
import dev.hauch.hMOTD.motd.MOTDManager;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class PingListener {

    private final AtomicReference<hMOTDConfig> config;
    private final MOTDManager motdManager;
    private final IconCache iconCache;
    private final TextFormatter formatter;

    public PingListener(AtomicReference<hMOTDConfig> config, MOTDManager motdManager, IconCache iconCache) {
        this.config = config;
        this.motdManager = motdManager;
        this.iconCache = iconCache;
        this.formatter = new TextFormatter(TextFormatter.Mode.MIXED);
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        hMOTDConfig cfg = config.get();
        ServerPing.Builder builder = event.getPing().asBuilder();

        builder.description(motdManager.nextMOTD());

        applyIcon(builder, cfg);
        applyPlayers(builder, cfg);
        applyVersion(builder, cfg);

        event.setPing(builder.build());
    }

    private void applyIcon(ServerPing.Builder builder, hMOTDConfig cfg) {
        if (!cfg.icon().enabled()) {
            return;
        }
        String icon = iconCache.getIcon(cfg.icon().file());
        if (icon != null) {
            builder.favicon(Favicon.create(icon));
        }
    }

    private void applyPlayers(ServerPing.Builder builder, hMOTDConfig cfg) {
        hMOTDConfig.ServerInfoConfig info = cfg.serverInfo();
        ServerPing.Players players = builder.getPlayers().orElse(null);

        int online = players != null ? players.getOnline() : 0;
        int max = players != null ? players.getMax() : 0;

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
            for (String line : info.sampleLines()) {
                Component component = formatter.format(line);
                sample.add(new ServerPing.SamplePlayer(formatter.serializeLegacy(component), java.util.UUID.randomUUID()));
            }
        } else if (players != null && players.getSample() != null) {
            sample = new ArrayList<>(players.getSample());
        }

        builder.samplePlayers(sample);
        if (online != -1 && max != -1) {
            builder.onlinePlayers(online);
            builder.maximumPlayers(max);
        }
    }

    private void applyVersion(ServerPing.Builder builder, hMOTDConfig cfg) {
        hMOTDConfig.ServerInfoConfig info = cfg.serverInfo();
        if (!info.overrideVersion()) {
            return;
        }
        int protocol = builder.getVersion().map(ServerPing.Version::getProtocol).orElse(0);
        Component versionComponent = formatter.format(info.versionText());
        builder.version(new ServerPing.Version(protocol, formatter.serializeLegacy(versionComponent)));
    }
}
