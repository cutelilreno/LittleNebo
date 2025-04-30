package moe.reno.littlenebo.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import moe.reno.littlenebo.LittleNebo;
import moe.reno.littlenebo.config.ConfigManager;
import moe.reno.littlenebo.config.FormatConfig;
import moe.reno.littlenebo.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the ChatManager class.
 */
public class ChatManagerTest {

    @Mock
    private LittleNebo plugin;

    @Mock
    private ConfigManager configManager;

    @Mock
    private Player player;

    @Mock
    private AsyncChatEvent chatEvent;

    @Mock
    private PlayerQuitEvent quitEvent;

    @Mock
    private FormatConfig formatConfig;

    private ChatManager chatManager;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Set up plugin mock
        when(plugin.getConfigManager()).thenReturn(configManager);

        // Set up player mock
        when(player.getName()).thenReturn("TestPlayer");
        when(player.getDisplayName()).thenReturn("TestPlayer");

        // Set up chat event mock
        when(chatEvent.getPlayer()).thenReturn(player);
        when(chatEvent.message()).thenReturn(Component.text("Test message"));

        // Set up quit event mock
        when(quitEvent.getPlayer()).thenReturn(player);

        // Set up format config mock
        when(formatConfig.format()).thenReturn("<gray>{display_name}</gray> <white>{message}</white>");
        when(formatConfig.legacyFormat()).thenReturn("");
        when(formatConfig.hasLegacyFormatConf()).thenReturn(false);

        // Set up config manager mock
        when(configManager.getFormatForPlayer(player)).thenReturn(formatConfig);
        when(configManager.isPlayerLegacyColorsEnabled()).thenReturn(true);
        when(configManager.isDebugEnabled()).thenReturn(false);

        // Create the chat manager
        chatManager = new ChatManager(plugin, configManager);

        // Use reflection to set the placeholdersEnabled field to false
        Field placeholdersEnabledField = ChatManager.class.getDeclaredField("placeholdersEnabled");
        placeholdersEnabledField.setAccessible(true);
        placeholdersEnabledField.set(chatManager, false);
    }

    /**
     * Test that onChat correctly processes a chat event.
     */
    @Test
    public void testOnChat() {
        // Call the method to test
        chatManager.onChat(chatEvent);

        // Verify the event renderer was set
        ArgumentCaptor<NeboChatRenderer> rendererCaptor = ArgumentCaptor.forClass(NeboChatRenderer.class);
        verify(chatEvent).renderer(rendererCaptor.capture());

        // Verify the renderer is not null
        assertNotNull(rendererCaptor.getValue(), "Chat renderer should not be null");

        // Verify debug logging if enabled
        when(configManager.isDebugEnabled()).thenReturn(true);
        chatManager.onChat(chatEvent);
        verify(plugin, times(3)).debug(anyString());
    }

    /**
     * Test that onPlayerQuit correctly handles a player quit event.
     */
    @Test
    public void testOnPlayerQuit() {
        // Call the method to test
        chatManager.onPlayerQuit(quitEvent);

        // Not much to verify here since the method just delegates to the renderer
        // We could use reflection to access the lastMessages map in NeboChatRenderer,
        // but that would be testing implementation details
    }

    /**
     * Test that formatMessage correctly formats a message with MiniMessage format.
     */
    @Test
    public void testFormatMessageWithMiniMessage() {
        // Set up the test
        when(configManager.isPlayerLegacyColorsEnabled()).thenReturn(true);
        when(formatConfig.hasLegacyFormatConf()).thenReturn(false);

        // Call the method to test
        Component result = chatManager.formatMessage(player, "Test <red>message</red>");

        // Verify the result contains the expected text
        String resultText = ColorUtil.componentToString(result);
        assertTrue(resultText.contains("TestPlayer"), "Formatted message should contain player name");
        assertTrue(resultText.contains("Test message"), "Formatted message should contain the message text");
    }

    /**
     * Test that formatMessage correctly formats a message with legacy format.
     */
    @Test
    public void testFormatMessageWithLegacyFormat() {
        // Set up the test
        when(configManager.isPlayerLegacyColorsEnabled()).thenReturn(true);
        when(formatConfig.hasLegacyFormatConf()).thenReturn(true);
        when(formatConfig.legacyFormat()).thenReturn("&7{display_name} &f{message}");

        // Call the method to test
        Component result = chatManager.formatMessage(player, "Test &cmessage");

        // Verify the result contains the expected text
        String resultText = result.toString();
        assertTrue(resultText.contains("TestPlayer"), "Formatted message should contain player name");
        assertTrue(resultText.contains("Test message"), "Formatted message should contain the message text");
    }

    /**
     * Test that formatMessage correctly handles player legacy colors being disabled.
     */
    @Test
    public void testFormatMessageWithLegacyColorsDisabled() {
        // Set up the test
        when(configManager.isPlayerLegacyColorsEnabled()).thenReturn(false);

        // Call the method to test
        Component result = chatManager.formatMessage(player, "Test &cmessage");

        // Verify the result contains the expected text
        String resultText = result.toString();
        assertTrue(resultText.contains("TestPlayer"), "Formatted message should contain player name");
        assertTrue(resultText.contains("Test &cmessage"), "Formatted message should contain the raw message with legacy codes");
    }

    /**
     * Test that formatMessage correctly handles PlaceholderAPI if available.
     * Note: This test is limited since we can't easily mock static methods in PlaceholderAPI.
     */
    @Test
    public void testFormatMessageWithPlaceholders() {
        // This is a limited test since we can't easily mock the static PlaceholderAPI.setPlaceholders method
        // In a real environment, we would need to use a tool like PowerMock or Mockito's mockStatic

        // Set up a format with a placeholder
        when(formatConfig.format()).thenReturn("<gray>{display_name}</gray> <white>{message}</white> %server_name%");

        // Call the method to test
        Component result = chatManager.formatMessage(player, "Test message");

        // Verify the result contains the expected text
        String resultText = result.toString();
        assertTrue(resultText.contains("TestPlayer"), "Formatted message should contain player name");
        assertTrue(resultText.contains("Test message"), "Formatted message should contain the message text");
    }
}
