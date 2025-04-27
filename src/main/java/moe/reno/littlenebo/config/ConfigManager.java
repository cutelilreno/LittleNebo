package moe.reno.littlenebo.config;

import moe.reno.littlenebo.LittleNebo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and access to the plugin's configuration values and chat formats.
 * <p>
 * This class handles reading the YAML config file, tracking runtime settings like debug
 * mode and color parsing, and building a registry of {@link FormatConfig} instances
 * for each named chat format section.
 * </p>
 */
public class ConfigManager {
    private final LittleNebo plugin;
    private boolean debug;
    private boolean parsePlayerColors;
    private int maxFormatChecks;
    private final Map<String, FormatConfig> formats = new HashMap<>();
    private FormatConfig defaultFormat;

    /**
     * Constructs a new ConfigManager tied to the given plugin instance.
     *
     * @param plugin the main plugin class (used to save/reload config and log)
     */
    public ConfigManager(LittleNebo plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads or reloads the configuration from disk.
     * <p>
     * - Saves the default config if missing.
     * - Reads general settings (debug, parse-player-colors, max-format-checks).
     * - Parses all defined chat format sections into {@link FormatConfig} objects.
     * - Ensures a fallback "default" format always exists.
     * </p>
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // Load debug setting
        debug = config.getBoolean("debug", false);

        // Load settings
        ConfigurationSection settings = config.getConfigurationSection("settings");

        parsePlayerColors = settings.getBoolean("parse-player-colors", false);
        maxFormatChecks = settings.getInt("max-format-checks", 10);

        // Load formats
        formats.clear();
        ConfigurationSection formatsSection = config.getConfigurationSection("formats");
        if (formatsSection != null) {
            for (String key : formatsSection.getKeys(false)) {
                ConfigurationSection formatSection = formatsSection.getConfigurationSection(key);
                if (formatSection != null) {
                    String format = formatSection.getString("format", "");
                    String legacyFormat = formatSection.getString("legacy-format", "");
                    String permission = formatSection.getString("permission", "");

                    FormatConfig formatConfig = new FormatConfig(format, legacyFormat, permission);
                    formats.put(key, formatConfig);

                    if (key.equals("default")) {
                        defaultFormat = formatConfig;
                    }
                }
            }
        }

        // Ensure we have a default format
        if (defaultFormat == null) {
            defaultFormat = new FormatConfig("<gray>{display_name}</gray><white>: {message}</white>", "", "");
            formats.put("default", defaultFormat);
        }

        plugin.getLogger().info("Loaded " + formats.size() + " chat formats");
    }

    /**
     * @return true if debug mode is currently enabled (detailed logging on)
     */
    public boolean isDebugEnabled() {
        return debug;
    }

    /**
     * Toggles the debug mode setting on or off, persists it to disk, and returns the new value.
     *
     * @return the updated debug state after toggling
     */
    public boolean toggleDebug() {
        debug = !debug;
        plugin.getConfig().set("debug", debug);
        plugin.saveConfig();
        return debug;
    }

    /**
     * @return whether player-entered color codes (ampersand '{@literal &}') should be parsed in chat
     */
    public boolean isParsePlayerColors() {
        return parsePlayerColors;
    }

    /**
     * @return the maximum number of permission-based format variants to check per message
     */
    public int getMaxFormatChecks() {
        return maxFormatChecks;
    }

    /**
     * Retrieves the appropriate {@link FormatConfig} for the given player.
     * <p>
     * Iterates through all configured formats in insertion order, returning the first
     * one whose permission node the player has. If none match, returns the "default" format.
     * </p>
     *
     * @param player the player whose permissions should be checked
     * @return the matching FormatConfig, or the default format if none match
     */
    public FormatConfig getFormatForPlayer(Player player) {
        for (Map.Entry<String, FormatConfig> entry : formats.entrySet()) {
            FormatConfig format = entry.getValue();
            if (!format.hasPermission()) {
                continue;
            }

            if (player.hasPermission(format.getPermission())) {
                return format;
            }
        }
        return defaultFormat;
    }
}