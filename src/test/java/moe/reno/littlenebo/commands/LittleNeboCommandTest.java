package moe.reno.littlenebo.commands;

import moe.reno.littlenebo.LittleNebo;
import moe.reno.littlenebo.chat.ChatManager;
import moe.reno.littlenebo.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the LittleNeboCommand class.
 */
public class LittleNeboCommandTest {

    @Mock
    private LittleNebo plugin;

    @Mock
    private ConfigManager configManager;

    @Mock
    private ChatManager chatManager;

    @Mock
    private CommandSender sender;

    @Mock
    private Player playerSender;

    @Mock
    private Command command;

    @Mock
    private PluginDescriptionFile description;

    private LittleNeboCommand littleNeboCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up plugin mock
        when(plugin.getConfigManager()).thenReturn(configManager);
        when(plugin.getChatManager()).thenReturn(chatManager);
        when(plugin.getDescription()).thenReturn(description);
        when(description.getVersion()).thenReturn("1.0.0");

        // Set up sender mock
        when(sender.hasPermission("littlenebo.admin")).thenReturn(true);

        // Set up player sender mock
        when(playerSender.hasPermission("littlenebo.admin")).thenReturn(true);

        // Create the command
        littleNeboCommand = new LittleNeboCommand(plugin);
    }

    /**
     * Test that onCommand handles the case where the sender doesn't have permission.
     */
    @Test
    public void testOnCommandNoPermission() {
        // Set up the test
        when(sender.hasPermission("littlenebo.admin")).thenReturn(false);

        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", new String[0]);

        // Verify the result
        assertTrue(result, "Command should return true even when permission is denied");

        // Verify the sender was sent an error message
        verify(sender).sendMessage(any(Component.class));
    }

    /**
     * Test that onCommand handles the case where no arguments are provided.
     */
    @Test
    public void testOnCommandNoArgs() {
        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", new String[0]);

        // Verify the result
        assertTrue(result, "Command should return true");

        // Verify the sender was sent the plugin info
        verify(sender, times(2)).sendMessage(any(Component.class));
    }

    /**
     * Test that onCommand handles the reload subcommand.
     */
    @Test
    public void testOnCommandReload() {
        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", new String[]{"reload"});

        // Verify the result
        assertTrue(result, "Command should return true");

        // Verify the config was reloaded
        verify(configManager).loadConfig();

        // Verify the sender was sent a success message
        verify(sender).sendMessage(any(Component.class));
    }

    /**
     * Test that onCommand handles the debug subcommand with no additional arguments.
     */
    @Test
    public void testOnCommandDebugToggle() {
        // Set up the test
        when(configManager.toggleDebug()).thenReturn(true);

        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", new String[]{"debug"});

        // Verify the result
        assertTrue(result, "Command should return true");

        // Verify debug was toggled
        verify(configManager).toggleDebug();

        // Verify the sender was sent a success message
        verify(sender).sendMessage(any(Component.class));
    }

    /**
     * Test that onCommand handles the debug config subcommand.
     */
    @Test
    public void testOnCommandDebugConfig() {
        // Set up the test
        when(configManager.isDebugEnabled()).thenReturn(true);
        when(configManager.isPlayerLegacyColorsEnabled()).thenReturn(true);

        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", new String[]{"debug", "config"});

        // Verify the result
        assertTrue(result, "Command should return true");

        // Verify the sender was sent the config info
        verify(sender, times(3)).sendMessage(any(Component.class));
    }

    /**
     * Test that onCommand handles the debug test subcommand.
     */
    @Test
    public void testOnCommandDebugTest() {
        // Set up the ChatManager mock to return a Component when formatMessage is called
        when(chatManager.formatMessage(any(Player.class), anyString())).thenReturn(Component.text("Formatted message"));

        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", 
                new String[]{"debug", "test", "Test", "message"});

        // Verify the result
        assertTrue(result, "Command should return true");

        // Verify the sender was sent the preview
        verify(sender, times(1)).sendMessage(any(Component.class));
    }

    /**
     * Test that onCommand handles the debug test subcommand with insufficient arguments.
     */
    @Test
    public void testOnCommandDebugTestInsufficientArgs() {
        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", new String[]{"debug", "test"});

        // Verify the result
        assertTrue(result, "Command should return true");

        // Verify the sender was sent an error message
        verify(sender).sendMessage(any(Component.class));
    }

    /**
     * Test that onCommand handles unknown debug subcommands.
     */
    @Test
    public void testOnCommandDebugUnknown() {
        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", new String[]{"debug", "unknown"});

        // Verify the result
        assertTrue(result, "Command should return true");

        // Verify the sender was sent an error message
        verify(sender, times(2)).sendMessage(any(Component.class));
    }

    /**
     * Test that onCommand handles unknown subcommands.
     */
    @Test
    public void testOnCommandUnknownSubcommand() {
        // Call the method to test
        boolean result = littleNeboCommand.onCommand(sender, command, "littlenebo", new String[]{"unknown"});

        // Verify the result
        assertTrue(result, "Command should return true");

        // Verify the sender was sent an error message
        verify(sender, times(2)).sendMessage(any(Component.class));
    }

    /**
     * Test that onTabComplete returns the correct completions.
     */
    @Test
    public void testOnTabComplete() {
        // Test with no args
        List<String> completions = littleNeboCommand.onTabComplete(sender, command, "littlenebo", new String[]{""});
        assertEquals(2, completions.size(), "Should return all subcommands when no args");
        assertTrue(completions.contains("reload"), "Completions should include 'reload'");
        assertTrue(completions.contains("debug"), "Completions should include 'debug'");

        // Test with partial arg
        completions = littleNeboCommand.onTabComplete(sender, command, "littlenebo", new String[]{"r"});
        assertEquals(1, completions.size(), "Should return matching subcommands");
        assertEquals("reload", completions.get(0), "Completion should be 'reload'");

        // Test with complete arg
        completions = littleNeboCommand.onTabComplete(sender, command, "littlenebo", new String[]{"reload"});
        assertNotNull(completions, "Should return a non-null list for complete arg");

        // Test with multiple args
        completions = littleNeboCommand.onTabComplete(sender, command, "littlenebo", new String[]{"debug", "test"});
        assertTrue(completions.isEmpty(), "Should return empty list for multiple args");
    }
}
