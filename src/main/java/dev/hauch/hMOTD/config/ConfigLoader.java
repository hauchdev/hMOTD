package dev.hauch.hMOTD.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads and saves the hMOTD YAML configuration, creating defaults when missing and migrating
 * outdated configuration values.
 */
public final class ConfigLoader {

    private final Path configPath;
    private final YamlConfigurationLoader loader;
    private final ObjectMapper<hMOTDConfig> mapper;

    /**
     * Creates a loader for the configuration inside the given plugin directory.
     *
     * @param pluginDirectory the plugin data directory
     */
    public ConfigLoader(Path pluginDirectory) {
        this.configPath = pluginDirectory.resolve("config.yml");
        this.loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .defaultOptions(opts -> opts
                        .shouldCopyDefaults(true)
                        .implicitInitialization(true)
                )
                .build();
        try {
            this.mapper = ObjectMapper.factory().get(hMOTDConfig.class);
        } catch (SerializationException e) {
            throw new RuntimeException("Failed to create config mapper", e);
        }
    }

    /**
     * Loads the configuration, creating it from the bundled default when it does not exist yet.
     *
     * @return the loaded configuration
     * @throws ConfigurateException when the file cannot be read or parsed
     */
    public hMOTDConfig load() throws ConfigurateException {
        ensureFileExists();
        CommentedConfigurationNode root = loader.load();

        boolean migrated = migrate(root);

        hMOTDConfig config;
        try {
            config = mapper.load(root);
        } catch (SerializationException e) {
            throw new ConfigurateException("Failed to deserialize config", e);
        }
        config.validate();

        if (migrated) {
            try {
                loader.save(root);
            } catch (ConfigurateException e) {
                throw new ConfigurateException("Failed to save migrated config", e);
            }
        }

        return config;
    }

    private void ensureFileExists() throws ConfigurateException {
        try {
            Files.createDirectories(configPath.getParent());
            if (!Files.exists(configPath)) {
                copyDefaultConfig();
            }
        } catch (Exception e) {
            throw new ConfigurateException("Could not create config file", e);
        }
    }

    private void copyDefaultConfig() {
        try (var in = getClass().getResourceAsStream("/config.yml")) {
            if (in != null) {
                Files.copy(in, configPath);
            } else {
                writeDefaults();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not copy default config", e);
        }
    }

    private void writeDefaults() throws ConfigurateException {
        CommentedConfigurationNode root = loader.createNode();
        try {
            mapper.save(new hMOTDConfig(), root);
        } catch (SerializationException e) {
            throw new ConfigurateException("Failed to serialize config defaults", e);
        }
        loader.save(root);
    }

    /**
     * Removes obsolete options and updates default values that changed between plugin versions.
     *
     * @return whether the configuration node was modified
     */
    private boolean migrate(CommentedConfigurationNode root) {
        boolean changed = false;

        if (!root.hasChild("center")) {
            return false;
        }

        ConfigurationNode center = root.node("center");

        if (center.hasChild("space-width")) {
            center.removeChild("space-width");
            changed = true;
        }

        if (center.hasChild("width")) {
            ConfigurationNode width = center.node("width");
            if (width.raw() instanceof Integer value && value == 248) {
                try {
                    width.set(270);
                    changed = true;
                } catch (SerializationException e) {
                    throw new IllegalStateException("Could not migrate centering width", e);
                }
            }
        }

        return changed;
    }
}
