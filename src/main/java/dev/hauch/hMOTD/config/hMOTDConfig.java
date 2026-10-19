package dev.hauch.hMOTD.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Root configuration object for hMOTD.
 */
@ConfigSerializable
public final class hMOTDConfig {

    @Comment("List of MOTDs to display. Supports MiniMessage, legacy '&' codes, and hex colors.")
    private List<String> motds = defaultMotds();

    @Comment("How to pick the MOTD: RANDOM, SEQUENTIAL, DISABLED")
    private RotationMode rotationMode = RotationMode.RANDOM;

    @Comment("Text format mode: MINIMESSAGE, LEGACY, MIXED")
    private FormatMode formatMode = FormatMode.MIXED;

    @Comment("Centering configuration")
    private CenterConfig center = new CenterConfig();

    @Comment("Server icon configuration")
    private IconConfig icon = new IconConfig();

    @Comment("Server info overrides")
    private ServerInfoConfig serverInfo = new ServerInfoConfig();

    @Comment("Enable placeholder replacement (%online%, %max%, %proxy_name%, etc.)")
    private boolean placeholders = true;

    @Comment("Enable debug logging")
    private boolean debug = false;

    @Comment("Custom placeholders available as %key% in MOTDs, version text and sample lines")
    private Map<String, String> extraPlaceholders = new HashMap<>();

    public hMOTDConfig() {
    }

    private static List<String> defaultMotds() {
        List<String> list = new ArrayList<>();
        list.add("<gradient:#00FF00:#0000FF>hMOTD</gradient> <white>Optimized MOTD");
        list.add("&aWelcome to &6&lhMOTD &r&7- &eSuper optimized");
        return list;
    }

    public List<String> motds() {
        return motds;
    }

    public RotationMode rotationMode() {
        return rotationMode;
    }

    public FormatMode formatMode() {
        return formatMode;
    }

    public CenterConfig center() {
        return center;
    }

    public IconConfig icon() {
        return icon;
    }

    public ServerInfoConfig serverInfo() {
        return serverInfo;
    }

    public boolean placeholders() {
        return placeholders;
    }

    public boolean debug() {
        return debug;
    }

    public Map<String, String> extraPlaceholders() {
        return extraPlaceholders;
    }

    @ConfigSerializable
    public static final class CenterConfig {
        @Comment("Enable automatic MOTD centering based on Minecraft font widths")
        private boolean enabled = true;

        @Comment("Maximum width in pixels used for centering calculations")
        private int width = 248;

        public CenterConfig() {
        }

        public boolean enabled() {
            return enabled;
        }

        public int width() {
            return width;
        }
    }

    @ConfigSerializable
    public static final class IconConfig {
        @Comment("Enable custom server icon")
        private boolean enabled = true;

        @Comment("Single icon file name inside the plugin directory")
        private String file = "server-icon.png";

        @Comment("List of icon files for rotation (overrides file when not empty)")
        private List<String> files = new ArrayList<>();

        @Comment("Icon rotation mode: RANDOM, SEQUENTIAL, DISABLED")
        private RotationMode rotationMode = RotationMode.RANDOM;

        public IconConfig() {
        }

        public boolean enabled() {
            return enabled;
        }

        public String file() {
            return file;
        }

        public List<String> files() {
            return files;
        }

        public RotationMode rotationMode() {
            return rotationMode;
        }
    }

    @ConfigSerializable
    public static final class ServerInfoConfig {
        @Comment("Override the version text shown to outdated clients")
        private boolean overrideVersion = false;

        @Comment("Version text (supports all formats)")
        private String versionText = "<#FF0000>hMOTD";

        @Comment("Override online and max player counts")
        private boolean overridePlayers = false;

        @Comment("Fake online player count (-1 to hide)")
        private int onlinePlayers = 0;

        @Comment("Fake max player count (-1 to hide)")
        private int maxPlayers = 100;

        @Comment("Hide online player count")
        private boolean hideOnline = false;

        @Comment("Hide max player count")
        private boolean hideMax = false;

        @Comment("Override player sample (hover text)")
        private boolean overrideSample = false;

        @Comment("Sample lines shown when hovering the player count")
        private List<String> sampleLines = defaultSample();

        public ServerInfoConfig() {
        }

        private static List<String> defaultSample() {
            List<String> list = new ArrayList<>();
            list.add("&aPowered by hMOTD");
            list.add("&bSuper optimized");
            return list;
        }

        public boolean overrideVersion() {
            return overrideVersion;
        }

        public String versionText() {
            return versionText;
        }

        public boolean overridePlayers() {
            return overridePlayers;
        }

        public int onlinePlayers() {
            return onlinePlayers;
        }

        public int maxPlayers() {
            return maxPlayers;
        }

        public boolean hideOnline() {
            return hideOnline;
        }

        public boolean hideMax() {
            return hideMax;
        }

        public boolean overrideSample() {
            return overrideSample;
        }

        public List<String> sampleLines() {
            return sampleLines;
        }
    }

    public enum RotationMode {
        RANDOM,
        SEQUENTIAL,
        DISABLED
    }

    public enum FormatMode {
        MINIMESSAGE,
        LEGACY,
        MIXED
    }
}
