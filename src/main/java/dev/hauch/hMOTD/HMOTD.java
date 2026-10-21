package dev.hauch.hMOTD;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.hauch.hMOTD.cache.IconCache;
import dev.hauch.hMOTD.cache.IconManager;
import dev.hauch.hMOTD.command.HMOTDCommand;
import dev.hauch.hMOTD.config.ConfigLoader;
import dev.hauch.hMOTD.config.hMOTDConfig;
import dev.hauch.hMOTD.listener.PingListener;
import dev.hauch.hMOTD.motd.MOTDManager;
import dev.hauch.hMOTD.placeholder.PlaceholderResolver;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main plugin class for hMOTD.
 * Wires configuration, the MOTD manager, icon rotation, command handling and the proxy ping listener.
 */
public class HMOTD {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;
    private final AtomicReference<hMOTDConfig> config;
    private final ConfigLoader configLoader;
    private MOTDManager motdManager;

    @Inject
    public HMOTD(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.configLoader = new ConfigLoader(dataDirectory);
        this.config = new AtomicReference<>(loadConfig());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Enabling hMOTD v${version}");

        PlaceholderResolver placeholderResolver = new PlaceholderResolver(proxyServer);
        this.motdManager = new MOTDManager(logger, placeholderResolver, config.get());
        IconCache iconCache = new IconCache(dataDirectory, logger);
        IconManager iconManager = new IconManager(iconCache, logger);

        EventManager eventManager = proxyServer.getEventManager();
        eventManager.register(this, new PingListener(config, motdManager, iconManager, placeholderResolver, logger));

        CommandManager commandManager = proxyServer.getCommandManager();
        commandManager.register(
                commandManager.metaBuilder("hmotd").build(),
                new HMOTDCommand(logger, configLoader, config, motdManager)
        );

        logger.info("hMOTD enabled with {} MOTD(s) in {} mode",
                config.get().motds().size(), config.get().rotationMode());
    }

    private hMOTDConfig loadConfig() {
        try {
            return configLoader.load();
        } catch (Exception e) {
            logger.error("Failed to load hMOTD configuration, using defaults", e);
            return new hMOTDConfig();
        }
    }
}
