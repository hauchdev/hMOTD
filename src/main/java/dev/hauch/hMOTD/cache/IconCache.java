package dev.hauch.hMOTD.cache;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

public final class IconCache {

    private static final String DATA_URI_PREFIX = "data:image/png;base64,";

    private final Path pluginDirectory;
    private final Logger logger;
    private final AtomicReference<String> cachedIcon = new AtomicReference<>();
    private volatile long lastModified;

    public IconCache(Path pluginDirectory, Logger logger) {
        this.pluginDirectory = pluginDirectory;
        this.logger = logger;
    }

    public String getIcon(String fileName) {
        Path iconPath = pluginDirectory.resolve(fileName);
        if (!Files.exists(iconPath)) {
            return null;
        }
        try {
            long modified = Files.getLastModifiedTime(iconPath).toMillis();
            String cached = cachedIcon.get();
            if (cached != null && modified == lastModified) {
                return cached;
            }
            byte[] bytes = Files.readAllBytes(iconPath);
            String encoded = DATA_URI_PREFIX + Base64.getEncoder().encodeToString(bytes);
            cachedIcon.set(encoded);
            lastModified = modified;
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded server icon from {}", iconPath);
            }
            return encoded;
        } catch (IOException e) {
            logger.warn("Failed to load server icon from {}", iconPath, e);
            return null;
        }
    }

    public void invalidate() {
        cachedIcon.set(null);
        lastModified = 0;
    }
}
