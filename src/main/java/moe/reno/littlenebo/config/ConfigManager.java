/**
 * MIT License
 * Copyright (c) 2025 cutelilreno
 * https://opensource.org/licenses/MIT
 */
package moe.reno.littlenebo.config;

import moe.reno.littlenebo.LittleNebo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final LittleNebo plugin;
    private boolean debug = false; // Default to false
    private boolean legacyPlayerColors;
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
    formats.clear();
    try {
        // Don't use plugin.reloadConfig() which will throw exceptions before we can catch them
        plugin.saveDefaultConfig();
        
        // Load the config file manually so we can catch YAML errors
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = new YamlConfiguration();
        
        try {
            config.load(configFile);
            // Set the config in the plugin
            Field configField = JavaPlugin.class.getDeclaredField("newConfig");
            configField.setAccessible(true);
            configField.set(plugin, config);
        } catch (IOException | InvalidConfigurationException e) {
            // This catches YAML syntax errors specifically
            handleConfigError("YAML syntax error - check indentation and formatting", e);
            return; // Important: stop processing here
        }
        
        // Continue with normal config processing
        debug = config.getBoolean("debug", isDebugEnabled());
        
        ConfigurationSection settings = config.getConfigurationSection("settings");
        legacyPlayerColors = settings != null && settings.getBoolean("parse-player-colors", true);
        
        loadChatFormats(config);
        
        plugin.getLogger().info("Loaded " + formats.size() + " chat formats");
    } catch (Exception e) {
        handleConfigError("unexpected error", e);
    }
}
    
    /**
     * Handles configuration errors in a consistent way
     * @param errorType description of the error type
     * @param e the exception that occurred
     */
    private void handleConfigError(String errorType, Exception e) {
        String message = e.getMessage();
        if (message != null && message.contains("\n")) {
            // For multi-line error messages, just take the first line
            message = message.substring(0, message.indexOf('\n'));
        }
        
        plugin.getLogger().severe("╔══ Nebo: Configuration Error ═══════════════════════");
        plugin.getLogger().severe("║ Failed to load config due to " + errorType);
        plugin.getLogger().severe("║ Error: " + message);
        
        if (errorType.contains("YAML")) {
            plugin.getLogger().severe("║ Fix: Check your config.yml indentation and syntax");
        }
        
        plugin.getLogger().severe("╠══ Using Default Settings ══════════════════");
        plugin.getLogger().severe("╚════════════════════════════════════════════");
        
        setupDefaultFormat();
    }
    /**
    * Loads chat formats from configuration
    */
    private void loadChatFormats(FileConfiguration config) {
        formats.clear(); // Clear existing formats
    
        ConfigurationSection formatsSection = config.getConfigurationSection("formats");
    
        if (formatsSection == null) {
            plugin.getLogger().warning("No 'formats' section found in config. Using default format.");
            setupDefaultFormat();
            return;
        }
    
        for (String key : formatsSection.getKeys(false)) {
            try {
                ConfigurationSection formatSection = formatsSection.getConfigurationSection(key);
                if (formatSection != null) {
                    String format = formatSection.getString("format", "");
                    String legacyFormat = formatSection.getString("legacy-format", "");
                    String permission = formatSection.getString("permission", "");
    
                    FormatConfig formatConfig = new FormatConfig(format, legacyFormat, permission);
                    formats.put(key, formatConfig);
    
                    // Set the default format if the key is "default"
                    if (key.equals("default")) {
                        defaultFormat = formatConfig;
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading format '" + key + "': " + e.getMessage());
            }
        }
    
        // Add this check to create a default format if none was found
        if (defaultFormat == null) {
            plugin.getLogger().warning("No default format found in config. Creating default format.");
            setupDefaultFormat();
        }
    }
    /**
     * Sets up a default format if none is found in the config.
     */
    private void setupDefaultFormat() {
        defaultFormat = new FormatConfig(
            "<gray>{display_name}</gray> <white>{message}</white>", // Default format
            "", // No legacy format
            ""  // No permission required
        );
        formats.put("default", defaultFormat);
        plugin.getLogger().info("Default format set to: " + defaultFormat.format());
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public boolean toggleDebug() {
        debug = !debug;
        plugin.getLogger().info("Debug mode is now " + (debug ? "enabled" : "disabled"));
        return debug;
    }

    public boolean saveConfigSafely() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = plugin.getConfig();

        try {
            // Validate the configuration by attempting to save it to a temporary file
            File tempFile = new File(plugin.getDataFolder(), "config_temp.yml");
            config.save(tempFile);

            // If successful, save the actual config
            config.save(configFile);
            if (tempFile.exists()) {
                tempFile.delete();
            }
            return true;
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null && message.contains("\n")) {
                // For multi-line error messages, just take the first line
                message = message.substring(0, message.indexOf('\n'));
            }
            plugin.getLogger().severe("╔══ Nebo: Configuration Error ═══════════════");
            plugin.getLogger().severe("║ Failed to save config due to " + message);
            plugin.getLogger().severe("║ Configuration changes were not saved");
            plugin.getLogger().severe("║ to prevent data loss.");
            plugin.getLogger().severe("╚════════════════════════════════════════════");
            return false;
        }
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