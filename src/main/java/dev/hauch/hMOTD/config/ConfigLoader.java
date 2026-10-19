package dev.hauch.hMOTD.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigLoader {

    private final Path configPath;
    private final YamlConfigurationLoader loader;
    private final ObjectMapper<hMOTDConfig> mapper;

    public ConfigLoader(Path pluginDirectory) {
        this.configPath = pluginDirectory.resolve("config.yml");
        this.loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .defaultOptions(opts -> opts
                        .shouldCopyDefaults(true)
                        .implicitInitialization(true)
                )
                .build();
        this.mapper = ObjectMapper.factory().get(hMOTDConfig.class);
    }

    public hMOTDConfig load() throws ConfigurateException {
        ensureFileExists();
        CommentedConfigurationNode root = loader.load();
        hMOTDConfig config = mapper.load(root);
        saveDefaults(config);
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
                saveDefaults(new hMOTDConfig());
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not copy default config", e);
        }
    }

    private void saveDefaults(hMOTDConfig config) throws ConfigurateException {
        CommentedConfigurationNode root = loader.createNode();
        mapper.save(config, root);
        loader.save(root);
    }
}
