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

    /**
     * Creates a configuration with the default values.
     */
    public hMOTDConfig() {
    }

    private static List<String> defaultMotds() {
        List<String> list = new ArrayList<>();
        list.add("<gradient:#00FF00:#0000FF>hMOTD</gradient> <white>Optimized MOTD");
        list.add("&aWelcome to &6&lhMOTD &r&7- &eSuper optimized");
        return list;
    }

    /**
     * The configured MOTDs.
     *
     * @return the configured MOTDs
     */
    public List<String> motds() {
        return motds;
    }

    /**
     * The MOTD rotation mode.
     *
     * @return the MOTD rotation mode
     */
    public RotationMode rotationMode() {
        return rotationMode;
    }

    /**
     * The text format mode.
     *
     * @return the text format mode
     */
    public FormatMode formatMode() {
        return formatMode;
    }

    /**
     * The alignment configuration.
     *
     * @return the alignment configuration
     */
    public CenterConfig center() {
        return center;
    }

    /**
     * The server icon configuration.
     *
     * @return the server icon configuration
     */
    public IconConfig icon() {
        return icon;
    }

    /**
     * The server info overrides.
     *
     * @return the server info overrides
     */
    public ServerInfoConfig serverInfo() {
        return serverInfo;
    }

    /**
     * Whether placeholders are replaced.
     *
     * @return whether placeholders are replaced
     */
    public boolean placeholders() {
        return placeholders;
    }

    /**
     * Whether debug logging is enabled.
     *
     * @return whether debug logging is enabled
     */
    public boolean debug() {
        return debug;
    }

    /**
     * The user defined placeholders.
     *
     * @return the user defined placeholders
     */
    public Map<String, String> extraPlaceholders() {
        return extraPlaceholders;
    }

    /**
     * Normalizes invalid configuration values to safe defaults.
     */
    public void validate() {
        if (center.width <= 0) {
            center.width = 270;
        }
        if (serverInfo.onlinePlayers < -1) {
            serverInfo.onlinePlayers = -1;
        }
        if (serverInfo.maxPlayers < -1) {
            serverInfo.maxPlayers = -1;
        }
        if (motds == null || motds.isEmpty()) {
            motds = new ArrayList<>();
            motds.add("<green>hMOTD</green> <white>default MOTD");
        }
    }

    /**
     * Alignment settings of the MOTD inside the server list text area.
     */
    @ConfigSerializable
    public static final class CenterConfig {

        @Comment("Enable automatic MOTD alignment based on Minecraft font metrics")
        private boolean enabled = true;

        @Comment("Alignment mode: LEFT, CENTER, RIGHT")
        private AlignMode mode = AlignMode.CENTER;

        @Comment("Width of the server list text area in pixels (270 matches the vanilla server list)")
        private int width = 270;

        @Comment("Fine tuning offset in pixels, positive moves the text to the right")
        private int offset = 0;

        /**
         * Creates the alignment settings with the default values.
         */
        public CenterConfig() {
        }

        /**
         * Whether alignment is enabled.
         *
         * @return whether alignment is enabled
         */
        public boolean enabled() {
            return enabled;
        }

        /**
         * Sets Enabled whether alignment is enabled..
         *
         * @param enabled whether alignment is enabled
         */
        public void enabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * The alignment mode.
         *
         * @return the alignment mode
         */
        public AlignMode mode() {
            return mode;
        }

        /**
         * Sets Mode the alignment mode..
         *
         * @param mode the alignment mode
         */
        public void mode(AlignMode mode) {
            this.mode = mode;
        }

        /**
         * The width of the text area in pixels.
         *
         * @return the width of the text area in pixels
         */
        public int width() {
            return width;
        }

        /**
         * Sets Width the width of the text area in pixels..
         *
         * @param width the width of the text area in pixels
         */
        public void width(int width) {
            this.width = width;
        }

        /**
         * The fine tuning offset in pixels.
         *
         * @return the fine tuning offset in pixels
         */
        public int offset() {
            return offset;
        }

        /**
         * Sets Offset the fine tuning offset in pixels..
         *
         * @param offset the fine tuning offset in pixels
         */
        public void offset(int offset) {
            this.offset = offset;
        }
    }

    /**
     * Server icon settings.
     */
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

        /**
         * Creates the icon settings with the default values.
         */
        public IconConfig() {
        }

        /**
         * Whether the custom icon is enabled.
         *
         * @return whether the custom icon is enabled
         */
        public boolean enabled() {
            return enabled;
        }

        /**
         * The single icon file name.
         *
         * @return the single icon file name
         */
        public String file() {
            return file;
        }

        /**
         * The list of icon files used for rotation.
         *
         * @return the list of icon files used for rotation
         */
        public List<String> files() {
            return files;
        }

        /**
         * The icon rotation mode.
         *
         * @return the icon rotation mode
         */
        public RotationMode rotationMode() {
            return rotationMode;
        }
    }

    /**
     * Overrides for the information shown in the server list entry.
     */
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

        /**
         * Creates the server info overrides with the default values.
         */
        public ServerInfoConfig() {
        }

        private static List<String> defaultSample() {
            List<String> list = new ArrayList<>();
            list.add("&aPowered by hMOTD");
            list.add("&bSuper optimized");
            return list;
        }

        /**
         * Whether the version text is overridden.
         *
         * @return whether the version text is overridden
         */
        public boolean overrideVersion() {
            return overrideVersion;
        }

        /**
         * The version text.
         *
         * @return the version text
         */
        public String versionText() {
            return versionText;
        }

        /**
         * Whether the player counts are overridden.
         *
         * @return whether the player counts are overridden
         */
        public boolean overridePlayers() {
            return overridePlayers;
        }

        /**
         * The fake online player count.
         *
         * @return the fake online player count
         */
        public int onlinePlayers() {
            return onlinePlayers;
        }

        /**
         * The fake maximum player count.
         *
         * @return the fake maximum player count
         */
        public int maxPlayers() {
            return maxPlayers;
        }

        /**
         * Whether the online player count is hidden.
         *
         * @return whether the online player count is hidden
         */
        public boolean hideOnline() {
            return hideOnline;
        }

        /**
         * Whether the maximum player count is hidden.
         *
         * @return whether the maximum player count is hidden
         */
        public boolean hideMax() {
            return hideMax;
        }

        /**
         * Whether the player sample is overridden.
         *
         * @return whether the player sample is overridden
         */
        public boolean overrideSample() {
            return overrideSample;
        }

        /**
         * The sample lines shown on hover.
         *
         * @return the sample lines shown on hover
         */
        public List<String> sampleLines() {
            return sampleLines;
        }
    }

    /**
     * Determines how the next MOTD is picked from the list.
     */
    public enum RotationMode {
        /**
         * Picks a random MOTD on every ping.
         */
        RANDOM,
        /**
         * Picks the next MOTD in the list.
         */
        SEQUENTIAL,
        /**
         * Always uses the first MOTD.
         */
        DISABLED
    }

    /**
     * Determines how the text of a MOTD is parsed.
     */
    public enum FormatMode {
        /**
         * Only MiniMessage tags are parsed.
         */
        MINIMESSAGE,
        /**
         * Only legacy ampersand codes are parsed.
         */
        LEGACY,
        /**
         * The format is detected automatically.
         */
        MIXED
    }

    /**
     * Determines how a MOTD line is aligned inside the server list text area.
     */
    public enum AlignMode {
        /**
         * Renders the line at the left edge of the text area.
         */
        LEFT,
        /**
         * Centers the line inside the text area.
         */
        CENTER,
        /**
         * Renders the line at the right edge of the text area.
         */
        RIGHT
    }
}
