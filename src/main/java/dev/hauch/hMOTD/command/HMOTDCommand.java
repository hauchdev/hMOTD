package dev.hauch.hMOTD.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import dev.hauch.hMOTD.config.ConfigLoader;
import dev.hauch.hMOTD.config.hMOTDConfig;
import dev.hauch.hMOTD.motd.MOTDManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles the /hmotd command including the reload and preview sub commands.
 */
public final class HMOTDCommand implements SimpleCommand {

    private final Logger logger;
    private final ConfigLoader configLoader;
    private final AtomicReference<hMOTDConfig> config;
    private final MOTDManager motdManager;

    public HMOTDCommand(Logger logger, ConfigLoader configLoader, AtomicReference<hMOTDConfig> config, MOTDManager motdManager) {
        this.logger = logger;
        this.configLoader = configLoader;
        this.config = config;
        this.motdManager = motdManager;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] arguments = invocation.arguments();
        if (arguments.length == 0) {
            sendHelp(invocation.source());
            return;
        }

        switch (arguments[0].toLowerCase(java.util.Locale.ROOT)) {
            case "reload" -> reload(invocation.source());
            case "preview" -> preview(invocation.source());
            default -> sendHelp(invocation.source());
        }
    }

    private void sendHelp(CommandSource source) {
        source.sendMessage(Component.text("hMOTD", NamedTextColor.AQUA)
                .append(Component.text(" - Velocity MOTD plugin", NamedTextColor.GRAY)));
        source.sendMessage(Component.text("/hmotd reload", NamedTextColor.YELLOW)
                .append(Component.text(" - reloads the configuration", NamedTextColor.GRAY)));
        source.sendMessage(Component.text("/hmotd preview", NamedTextColor.YELLOW)
                .append(Component.text(" - shows the rendered MOTD", NamedTextColor.GRAY)));
    }

    private void reload(CommandSource source) {
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

    private void preview(CommandSource source) {
        hMOTDConfig cfg = config.get();
        Component motd = motdManager.nextMOTD();
        String plain = PlainTextComponentSerializer.plainText().serialize(motd);

        source.sendMessage(Component.text("hMOTD preview", NamedTextColor.AQUA)
                .append(Component.text(" | mode: ", NamedTextColor.GRAY))
                .append(Component.text(cfg.center().mode().name(), NamedTextColor.WHITE))
                .append(Component.text(" | width: ", NamedTextColor.GRAY))
                .append(Component.text(cfg.center().width() + "px", NamedTextColor.WHITE))
                .append(Component.text(" | offset: ", NamedTextColor.GRAY))
                .append(Component.text(cfg.center().offset() + "px", NamedTextColor.WHITE)));
        source.sendMessage(Component.text(plain, NamedTextColor.WHITE));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("hmotd.reload");
    }
}
