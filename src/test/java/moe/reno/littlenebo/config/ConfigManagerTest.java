package moe.reno.littlenebo.config;

import moe.reno.littlenebo.LittleNebo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the ConfigManager class.
 */
class ConfigManagerTest {

    @Mock
    private LittleNebo plugin;

    @Mock
    private FileConfiguration config;

    @Mock
    private ConfigurationSection settingsSection;

    @Mock
    private ConfigurationSection formatsSection;

    @Mock
    private ConfigurationSection defaultFormatSection;

    @Mock
    private ConfigurationSection vipFormatSection;

    @Mock
    private Player player;

    @Mock
    private Logger logger;

    private ConfigManager configManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up the plugin mock
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getLogger()).thenReturn(logger);

        // Set up the config mock
        when(config.getBoolean("debug", false)).thenReturn(false);
        when(config.getConfigurationSection("settings")).thenReturn(settingsSection);
        when(config.getConfigurationSection("formats")).thenReturn(formatsSection);

        // Set up settings section
        when(settingsSection.getBoolean("parse-player-colors", true)).thenReturn(true);

        // Set up formats section
        when(formatsSection.getKeys(false)).thenReturn(java.util.Set.of("default", "vip"));
        when(formatsSection.getConfigurationSection("default")).thenReturn(defaultFormatSection);
        when(formatsSection.getConfigurationSection("vip")).thenReturn(vipFormatSection);

        // Set up default format section
        when(defaultFormatSection.getString("format", "")).thenReturn("<gray>{display_name}</gray> <white>{message}</white>");
        when(defaultFormatSection.getString("legacy-format", "")).thenReturn("");
        when(defaultFormatSection.getString("permission", "")).thenReturn("");

        // Set up VIP format section
        when(vipFormatSection.getString("format", "")).thenReturn("<gold>{display_name}</gold> <yellow>{message}</yellow>");
        when(vipFormatSection.getString("legacy-format", "")).thenReturn("");
        when(vipFormatSection.getString("permission", "")).thenReturn("littlenebo.vip");

        // Create the ConfigManager
        configManager = new ConfigManager(plugin);
    }

    /**
     * Test that loadConfig correctly loads configuration from the plugin.
     */
    @Test
    void testLoadConfig() {
        // Call the method to test
        configManager.loadConfig();

        // Verify interactions
        verify(plugin).saveDefaultConfig();
        verify(plugin).reloadConfig();
        verify(plugin).getConfig();
        verify(config).getBoolean("debug", false);
        verify(config).getConfigurationSection("settings");
        verify(config).getConfigurationSection("formats");

        // Verify logger was called with the correct message
        verify(plugin.getLogger()).info(contains("Loaded"));
    }

    /**
     * Test that isDebugEnabled returns the correct value.
     */
    @Test
    void testIsDebugEnabled() {
        // Set up the mock to return true for debug
        when(config.getBoolean("debug", false)).thenReturn(true);

        // Load the config
        configManager.loadConfig();

        // Verify debug is enabled
        assertTrue(configManager.isDebugEnabled(), "Debug should be enabled");

        // Set up the mock to return false for debug
        when(config.getBoolean("debug", false)).thenReturn(false);

        // Load the config again
        configManager.loadConfig();

        // Verify debug is disabled
        assertFalse(configManager.isDebugEnabled(), "Debug should be disabled");
    }

    /**
     * Test that toggleDebug correctly toggles the debug setting.
     */
    @Test
    void testToggleDebug() {
        // Set up initial state
        when(config.getBoolean("debug", false)).thenReturn(false);
        configManager.loadConfig();

        // First toggle should enable debug
        when(config.getBoolean("debug", false)).thenReturn(true);
        boolean result = configManager.toggleDebug();

        // Verify result and interactions
        assertTrue(result, "Toggle should return true (enabled)");
        verify(config).set("debug", true);
        verify(plugin).saveConfig();

        // Second toggle should disable debug
        when(config.getBoolean("debug", false)).thenReturn(false);
        result = configManager.toggleDebug();

        // Verify result and interactions
        assertFalse(result, "Toggle should return false (disabled)");
        verify(config).set("debug", false);
    }

    /**
     * Test that isPlayerLegacyColorsEnabled returns the correct value.
     */
    @Test
    void testIsPlayerLegacyColorsEnabled() {
        // Set up the mock to return true
        when(settingsSection.getBoolean("parse-player-colors", true)).thenReturn(true);

        // Load the config
        configManager.loadConfig();

        // Verify legacy colors are enabled
        assertTrue(configManager.isPlayerLegacyColorsEnabled(), "Legacy colors should be enabled");

        // Set up the mock to return false
        when(settingsSection.getBoolean("parse-player-colors", true)).thenReturn(false);

        // Load the config again
        configManager.loadConfig();

        // Verify legacy colors are disabled
        assertFalse(configManager.isPlayerLegacyColorsEnabled(), "Legacy colors should be disabled");
    }

    /**
     * Test that getFormatForPlayer returns the correct format based on permissions.
     */
    @Test
    void testGetFormatForPlayer() {
        // Load the config
        configManager.loadConfig();

        // Player without VIP permission
        when(player.hasPermission("littlenebo.vip")).thenReturn(false);

        // Get format for player
        FormatConfig format = configManager.getFormatForPlayer(player);

        // Verify it's the default format
        assertEquals("<gray>{display_name}</gray> <white>{message}</white>", format.format(), 
                "Player without permission should get default format");

        // Player with VIP permission
        when(player.hasPermission("littlenebo.vip")).thenReturn(true);

        // Get format for player
        format = configManager.getFormatForPlayer(player);

        // Verify it's the VIP format
        assertEquals("<gold>{display_name}</gold> <yellow>{message}</yellow>", format.format(), 
                "Player with permission should get VIP format");
    }

    /**
     * Test that the default format is created if missing from config.
     */
    @Test
    void testDefaultFormatFallback() {
        // Set up the formats section to return null for default
        when(formatsSection.getConfigurationSection("default")).thenReturn(null);

        // Load the config
        configManager.loadConfig();

        // Get format for player (should fall back to default)
        FormatConfig format = configManager.getFormatForPlayer(player);

        // Verify it's the fallback default format
        assertNotNull(format, "Format should not be null");
        assertTrue(format.format().contains("<gray>"), "Format should contain default styling");
    }
}
