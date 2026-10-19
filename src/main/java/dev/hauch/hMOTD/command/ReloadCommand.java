package dev.hauch.hMOTD.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import dev.hauch.hMOTD.config.ConfigLoader;
import dev.hauch.hMOTD.config.hMOTDConfig;
import dev.hauch.hMOTD.motd.MOTDManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public final class ReloadCommand implements SimpleCommand {

    private final Logger logger;
    private final ConfigLoader configLoader;
    private final AtomicReference<hMOTDConfig> config;
    private final MOTDManager motdManager;

    public ReloadCommand(Logger logger, ConfigLoader configLoader, AtomicReference<hMOTDConfig> config, MOTDManager motdManager) {
        this.logger = logger;
        this.configLoader = configLoader;
        this.config = config;
        this.motdManager = motdManager;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        try {
            hMOTDConfig newConfig = configLoader.load();
            config.set(newConfig);
            motdManager.reload(newConfig);
            source.sendMessage(Component.text("hMOTD configuration reloaded successfully.", NamedTextColor.GREEN));
            logger.info("hMOTD configuration reloaded by {}", source);
        } catch (Exception e) {
            source.sendMessage(Component.text("Failed to reload hMOTD configuration. Check console.", NamedTextColor.RED));
            logger.error("Failed to reload hMOTD configuration", e);
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("hmotd.reload");
    }
}
