package dev.hauch.hMOTD.cache;

import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Caches the path of the server icon and invalidates it when the file changes.
 */
public final class IconCache {

    private final Path pluginDirectory;
    private final Logger logger;
    private final AtomicReference<Path> cachedPath = new AtomicReference<>();
    private volatile long lastModified;

    /**
     * Creates a cache for icons stored in the given directory.
     *
     * @param pluginDirectory the plugin data directory
     * @param logger          the plugin logger
     */
    public IconCache(Path pluginDirectory, Logger logger) {
        this.pluginDirectory = pluginDirectory;
        this.logger = logger;
    }

    /**
     * Resolves the path of the given icon file, caching the result until the file changes.
     *
     * @param fileName the icon file name relative to the plugin directory
     * @return the icon path or {@code null} when the file does not exist
     */
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

    /**
     * Drops the cached icon so it is resolved again on the next request.
     */
    public void invalidate() {
        cachedPath.set(null);
        lastModified = 0;
    }
}
