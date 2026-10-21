package dev.hauch.hMOTD.cache;

import dev.hauch.hMOTD.config.hMOTDConfig;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selects the next server icon according to the configured rotation mode.
 */
public final class IconManager {

    private final IconCache iconCache;
    private final Logger logger;
    private final AtomicInteger sequentialIndex = new AtomicInteger(0);

    /**
     * Creates an icon manager.
     *
     * @param iconCache the icon path cache
     * @param logger    the plugin logger
     */
    public IconManager(IconCache iconCache, Logger logger) {
        this.iconCache = iconCache;
        this.logger = logger;
    }

    /**
     * Selects the next icon according to the configured rotation mode.
     *
     * @param config the icon configuration
     * @return the selected icon path or {@code null} when no icon is available
     */
    public Path nextIcon(hMOTDConfig.IconConfig config) {
        if (!config.enabled()) {
            return null;
        }
        List<String> files = config.files();
        String fileName;
        if (files == null || files.isEmpty()) {
            fileName = config.file();
        } else {
            fileName = selectFile(files, config.rotationMode());
        }
        return iconCache.getIcon(fileName);
    }

    private String selectFile(List<String> files, hMOTDConfig.RotationMode mode) {
        return switch (mode) {
            case RANDOM -> files.get(ThreadLocalRandom.current().nextInt(files.size()));
            case SEQUENTIAL -> {
                int index = sequentialIndex.getAndUpdate(i -> (i + 1) % files.size());
                yield files.get(index);
            }
            case DISABLED -> files.get(0);
        };
    }
}
