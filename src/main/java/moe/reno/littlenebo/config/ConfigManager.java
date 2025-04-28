package moe.reno.littlenebo.config;

import moe.reno.littlenebo.LittleNebo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final LittleNebo plugin;
    private boolean debug;
    private boolean legacyPlayerColors;
    private int maxFormatChecks;
    private final Map<String, FormatConfig> formats = new HashMap<>();
    private FormatConfig defaultFormat;

    public ConfigManager(LittleNebo plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads or reloads the configuration from disk.
     * <p>
     * - Creates default conf, if necessary.
     * - Load config and parses chat format
     * </p>
     */
    public void loadConfig() {
        // TODO: Rewrite to fail more gracefully with yaml errors. Currently it's works as intended, but spams console
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        formats.clear();


        debug = config.getBoolean("debug", false);

        ConfigurationSection settings = config.getConfigurationSection("settings");
        legacyPlayerColors = settings.getBoolean("parse-player-colors", true);

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

        // Temp fix allows plugin to still work if syntax error in conf. Will need to rewrite loadConfig()
        if (defaultFormat == null) {
            defaultFormat = new FormatConfig("<gray><{display_name}></gray> <white>{message}</white>", "", "true");
            formats.put("default", defaultFormat);
        }

        plugin.getLogger().info("Loaded " + formats.size() + " chat formats");
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public boolean toggleDebug() {
        debug = !debug;
        plugin.getConfig().set("debug", debug);
        plugin.saveConfig();
        loadConfig(); // bit of a weird hack, but harmless
        return debug;
    }

    public boolean isPlayerLegacyColorsEnabled() {
        return legacyPlayerColors;
    }

    public FormatConfig getFormatForPlayer(Player player) {
        for (Map.Entry<String, FormatConfig> entry : formats.entrySet()) {
            FormatConfig format = entry.getValue();

            if (format.hasPermission() && player.hasPermission(format.groupPermission())) {
                return format;
            }
        }
        return defaultFormat;
    }
}