/**
 * MIT License
 *
 * Copyright (c) 2025 cutelilreno
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package moe.reno.littlenebo.config;

import moe.reno.littlenebo.LittleNebo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final LittleNebo plugin;
    private boolean debug;
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
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
        
            FileConfiguration config = plugin.getConfig();
        
            debug = config.getBoolean("debug", false);
        
            ConfigurationSection settings = config.getConfigurationSection("settings");
            legacyPlayerColors = settings != null && settings.getBoolean("parse-player-colors", true);
        
            loadChatFormats(config);
        
            plugin.getLogger().info("Loaded " + formats.size() + " chat formats");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load configuration: " + e.getMessage());
            plugin.getLogger().warning("Please check your config.yml for errors");
            setupDefaultFormat();
        }

        

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