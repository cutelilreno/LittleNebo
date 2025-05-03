package moe.reno.littlenebo.config;

import moe.reno.littlenebo.LittleNebo;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigManagerTest {

    private ConfigManager configManager;
    private LittleNebo plugin;
    private FileConfiguration config;
    private Logger logger;

    @BeforeEach
    void setUp() {
        plugin = mock(LittleNebo.class);
        config = mock(FileConfiguration.class);
        logger = mock(Logger.class);  // Create a mock logger

        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getDataFolder()).thenReturn(new File("/tmp"));
        when(plugin.getLogger()).thenReturn(logger);  // Mock the getLogger method

        configManager = new ConfigManager(plugin);
    }

    @Test
    void testToggleDebug() {
        // Initial state
        when(config.getBoolean("debug", false)).thenReturn(false);
        assertFalse(configManager.isDebugEnabled());

        // Toggle debug on
        boolean debugState = configManager.toggleDebug();
        assertTrue(debugState);
        assertTrue(configManager.isDebugEnabled());

        // Toggle debug off
        debugState = configManager.toggleDebug();
        assertFalse(debugState);
        assertFalse(configManager.isDebugEnabled());
    }

    @Test
    void testSaveConfigSafely_ValidConfig() throws IOException {
        File tempFile = new File("/tmp/config_temp.yml");
        File configFile = new File("/tmp/config.yml");

        // Mock successful save
        doNothing().when(config).save(tempFile);
        doNothing().when(config).save(configFile);

        boolean result = configManager.saveConfigSafely();
        assertTrue(result);

        // Verify interactions
        verify(config, times(1)).save(tempFile);
        verify(config, times(1)).save(configFile);
    }

    @Test
    void testSaveConfigSafely_InvalidConfig() throws IOException {
        File tempFile = new File("/tmp/config_temp.yml");

        // Mock save failure
        doThrow(new IOException("Invalid config"))
                .when(config).save(tempFile);

        boolean result = configManager.saveConfigSafely();
        assertFalse(result);

        // Verify interactions
        verify(config, times(1)).save(tempFile);
        verify(config, never()).save(new File("/tmp/config.yml"));
    }
}