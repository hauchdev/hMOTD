package dev.hauch.hMOTD.motd;

import dev.hauch.hMOTD.center.TextAligner;
import dev.hauch.hMOTD.config.hMOTDConfig;
import dev.hauch.hMOTD.format.TextFormatter;
import dev.hauch.hMOTD.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Selects, caches and renders the next MOTD according to the active configuration.
 * <p>
 * The rendering pipeline is: raw text -> placeholder replacement -> per line formatting ->
 * optional alignment -> line joining.
 */
public final class MOTDManager {

    private final Logger logger;
    private final PlaceholderResolver placeholderResolver;
    private final AtomicReference<hMOTDConfig> config;
    private final AtomicReference<TextFormatter> formatter;
    private final AtomicInteger sequentialIndex = new AtomicInteger(0);
    private final ConcurrentHashMap<String, Component> cache = new ConcurrentHashMap<>();

    public MOTDManager(Logger logger, PlaceholderResolver placeholderResolver, hMOTDConfig initialConfig) {
        this.logger = logger;
        this.placeholderResolver = placeholderResolver;
        this.config = new AtomicReference<>(initialConfig);
        this.formatter = new AtomicReference<>(createFormatter(initialConfig));
    }

    public void reload(hMOTDConfig newConfig) {
        this.config.set(newConfig);
        this.formatter.set(createFormatter(newConfig));
        this.cache.clear();
        this.sequentialIndex.set(0);
        if (logger.isDebugEnabled()) {
            logger.debug("MOTDManager reloaded with {} MOTD(s)", newConfig.motds().size());
        }
    }

    private TextFormatter createFormatter(hMOTDConfig config) {
        return new TextFormatter(TextFormatter.Mode.valueOf(config.formatMode().name()));
    }

    public Component nextMOTD() {
        hMOTDConfig cfg = config.get();
        List<String> motds = cfg.motds();
        if (motds.isEmpty()) {
            return Component.empty();
        }

        String raw = selectRaw(motds, cfg.rotationMode());
        if (raw == null || raw.isEmpty()) {
            return Component.empty();
        }

        return cache.computeIfAbsent(raw, this::buildComponent);
    }

    private String selectRaw(List<String> motds, hMOTDConfig.RotationMode mode) {
        return switch (mode) {
            case RANDOM -> motds.get(ThreadLocalRandom.current().nextInt(motds.size()));
            case SEQUENTIAL -> {
                int index = sequentialIndex.getAndUpdate(i -> (i + 1) % motds.size());
                yield motds.get(index);
            }
            case DISABLED -> motds.get(0);
        };
    }

    private Component buildComponent(String raw) {
        hMOTDConfig cfg = config.get();
        String withPlaceholders = cfg.placeholders()
                ? placeholderResolver.resolve(raw, cfg.extraPlaceholders())
                : raw;

        TextFormatter activeFormatter = formatter.get();
        List<Component> lines = activeFormatter.formatLines(withPlaceholders);

        if (cfg.center().enabled()) {
            TextAligner.Mode mode = TextAligner.Mode.valueOf(cfg.center().mode().name());
            int width = cfg.center().width();
            int offset = cfg.center().offset();
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, TextAligner.align(lines.get(i), mode, width, offset));
            }
        }

        Component result = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            result = result.append(lines.get(i));
            if (i < lines.size() - 1) {
                result = result.append(Component.newline());
            }
        }
        return result;
    }
}
