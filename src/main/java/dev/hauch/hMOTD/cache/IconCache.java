package dev.hauch.hMOTD.cache;

import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public final class IconCache {

    private final Path pluginDirectory;
    private final Logger logger;
    private final AtomicReference<Path> cachedPath = new AtomicReference<>();
    private volatile long lastModified;

    public IconCache(Path pluginDirectory, Logger logger) {
        this.pluginDirectory = pluginDirectory;
        this.logger = logger;
    }

    public Path getIcon(String fileName) {
        Path iconPath = pluginDirectory.resolve(fileName);
        if (!Files.exists(iconPath)) {
            return null;
        }
        try {
            long modified = Files.getLastModifiedTime(iconPath).toMillis();
            Path cached = cachedPath.get();
            if (cached != null && modified == lastModified) {
                return cached;
            }
            cachedPath.set(iconPath);
            lastModified = modified;
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded server icon from {}", iconPath);
            }
            return iconPath;
        } catch (Exception e) {
            logger.warn("Failed to load server icon from {}", iconPath, e);
            return null;
        }
    }

    public void invalidate() {
        cachedPath.set(null);
        lastModified = 0;
    }
}
