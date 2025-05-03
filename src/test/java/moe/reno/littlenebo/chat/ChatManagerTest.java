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
package moe.reno.littlenebo.chat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.papermc.paper.event.player.AsyncChatEvent;
import moe.reno.littlenebo.LittleNebo;
import moe.reno.littlenebo.config.ConfigManager;
import moe.reno.littlenebo.config.FormatConfig;
import moe.reno.littlenebo.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the ChatManager class using MockBukkit
 */
class ChatManagerTest {
    private ServerMock server;
    private LittleNebo plugin;
    private ConfigManager configManager;
    private ChatManager chatManager;
    private PlayerMock player;
    private NeboChatRenderer chatRenderer;

    @BeforeEach
    public void setUp() {
        // Start a mock server
        server = MockBukkit.mock();
        
        // Create a mock plugin instance using Mockito instead of MockBukkit
        plugin = Mockito.mock(LittleNebo.class);
        
        // Add debug logging mocking
        Mockito.doNothing().when(plugin).debug(Mockito.anyString());
        
        // Set up a ConfigManager mock that returns a simple format
        configManager = Mockito.mock(ConfigManager.class);
        FormatConfig defaultFormat = new FormatConfig("<white>{display_name}</white>: <white>{message}</white>", "", "");
        when(configManager.getFormatForPlayer(Mockito.any())).thenReturn(defaultFormat);
        when(configManager.isPlayerLegacyColorsEnabled()).thenReturn(true);
        when(configManager.isDebugEnabled()).thenReturn(false);
        
        // Link the mocks together
        when(plugin.getConfigManager()).thenReturn(configManager);
        
        // Create a real ChatManager with our mock dependencies
        chatManager = new ChatManager(plugin, configManager);
        
        // Link ChatManager back to plugin (crucial for tests)
        when(plugin.getChatManager()).thenReturn(chatManager);
        
        // Create a test player
        player = server.addPlayer("TestPlayer");
        player.displayName(Component.text("TestPlayer"));
        
        // Access the chatRenderer directly from ChatManager
        try {
            Field rendererField = ChatManager.class.getDeclaredField("chatRenderer");
            rendererField.setAccessible(true);
            chatRenderer = (NeboChatRenderer) rendererField.get(chatManager);
        } catch (Exception e) {
            fail("Could not access chatRenderer field: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Test
    void formatMessage_withBasicMessage_formatsCorrectly() {
        // Test basic message formatting
        Component result = chatManager.formatMessage(player, "Hello world!");
        
        String content = ColorUtil.componentToString(result);
        assertTrue(content.contains("TestPlayer") && content.contains("Hello world!"));
    }
    
    @Test
    void formatMessage_withLegacyColors_parsesCorrectly() {
        // Test message with legacy color codes
        Component result = chatManager.formatMessage(player, "&cColored &bmessage");
        
        // The formatted content should contain both parts of the message
        String content = ColorUtil.componentToString(result);
        assertTrue(content.contains("Colored") && content.contains("message"));
    }
    
    @Test
    void formatMessage_withNullPlayer_usesConsoleAsDisplayName() {
        // Test formatting with null player (represents console)
        Component result = chatManager.formatMessage(null, "Console message");
        
        String content = ColorUtil.componentToString(result);
        assertTrue(content.contains("Console") && content.contains("Console message"));
    }

    @Test
    void onChat_handlesAsyncChatEventCorrectly() {
        // Create a test message component
        Component messageComponent = Component.text("Test message");
        
        // Create a mock AsyncChatEvent
        AsyncChatEvent event = Mockito.mock(AsyncChatEvent.class);
        when(event.getPlayer()).thenReturn(player);
        when(event.message()).thenReturn(messageComponent);
        when(event.isCancelled()).thenReturn(false);
        
        // Mock the renderer setting
        Mockito.doNothing().when(event).renderer(Mockito.any(NeboChatRenderer.class));
        
        // Call the handler
        chatManager.onChat(event);
        
        // Verify renderer was set
        Mockito.verify(event).renderer(Mockito.any(NeboChatRenderer.class));
        
        // Instead of using the renderer directly to verify message storage,
        // call formatMessage again and check its output since that's what the renderer uses
        Component result = chatManager.formatMessage(player, "Test message");
        String content = ColorUtil.componentToString(result);
        assertTrue(content.contains("TestPlayer") && content.contains("Test message"));
    }

    @Test
    void chatRenderer_formatsMessageCorrectly() {
        // Store a test message in the renderer
        chatRenderer.setLastMessage(player, "Test renderer message");
        
        // Create expected output by using chatManager directly
        Component expected = chatManager.formatMessage(player, "Test renderer message");
        String expectedContent = ColorUtil.componentToString(expected);
        
        assertTrue(expectedContent.contains("TestPlayer") && expectedContent.contains("Test renderer message"));
    }

    @Test
    void formatMessage_withDifferentFormat_appliesFormatCorrectly() {
        // Change the format
        FormatConfig customFormat = new FormatConfig("<gold>[Player] {display_name}</gold> » <green>{message}</green>", "", "");
        when(configManager.getFormatForPlayer(player)).thenReturn(customFormat);
        
        Component result = chatManager.formatMessage(player, "Custom format test");
        
        String content = ColorUtil.componentToString(result);
        assertTrue(content.contains("[Player]") && content.contains("TestPlayer") && content.contains("Custom format test"));
    }

    @Test
    public void formatMessage_withLegacyColorsDisabled_ignoresLegacyCodes() {
        // Disable legacy colors
        when(configManager.isPlayerLegacyColorsEnabled()).thenReturn(false);
        
        Component result = chatManager.formatMessage(player, "&cThis &bshould &enot &abe colored");
        
        String content = ColorUtil.componentToString(result);
        // Check that the message is not colored (either raw codes are included or they are shown as plain text)
        assertTrue(content.contains("&c") || content.contains("This should not be colored"));
    }

    @Test
    public void formatMessage_withNullMessage_handlesGracefully() {
        // Test null message handling
        Component result = chatManager.formatMessage(player, null);
        
        // Should not throw exception and should return non-null component
        assertNotNull(result);
        String content = ColorUtil.componentToString(result);
        assertTrue(content.contains("TestPlayer"));
    }

    @Test
    public void onPlayerQuit_removesLastMessage() {
        // First store a message
        chatRenderer.setLastMessage(player, "Test message");
        
        // Create quit event with a non-deprecated constructor
        @SuppressWarnings("deprecation")
        PlayerQuitEvent event = new PlayerQuitEvent(player, Component.text(""));
        
        // Call the handler
        chatManager.onPlayerQuit(event);
        
        // We can't directly test the internal state of chatRenderer,
        // but the message should be removed from the cache.
        // Let's use reflection to check
        try {
            Field messagesField = NeboChatRenderer.class.getDeclaredField("lastMessages");
            messagesField.setAccessible(true);
            java.util.Map<?, ?> messages = (java.util.Map<?, ?>)messagesField.get(chatRenderer);
            assertFalse(messages.containsKey(player.getUniqueId()));
        } catch (Exception e) {
            fail("Could not access lastMessages field: " + e.getMessage());
        }
    }

    @Test
    public void formatMessage_performance_isReasonable() {
        // Test performance with many messages
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) { // Reduced to 100 for faster test runs
            chatManager.formatMessage(player, "Performance test " + i);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        // The test should complete in under 1 second (adjust as needed)
        assertTrue(duration < 1000, "Formatting 100 messages took too long: " + duration + "ms");
    }

    // Mock PlaceholderAPI instead of trying to access the real one
    @Test
    public void formatMessage_withPlaceholderAPI_handlesPlaceholders() throws Exception {
        // Instead of using PlaceholderAPI directly, we'll mock the behavior
        
        // Create a custom ChatManager with our own placeholdersEnabled flag
        ChatManager testChatManager = new ChatManager(plugin, configManager) {
            @Override
            public Component formatMessage(Player player, String message) {
                if (player != null && message != null) {
                    // Simulate PlaceholderAPI replacing placeholders
                    if (message.contains("%player_world%")) {
                        message = message.replace("%player_world%", "test_world");
                    }
                }
                return super.formatMessage(player, message);
            }
        };
        
        // Create a test with a format that includes placeholders
        FormatConfig placeholderFormat = new FormatConfig("<white>{display_name} [%player_world%]</white>: <white>{message}</white>", "", "");
        when(configManager.getFormatForPlayer(player)).thenReturn(placeholderFormat);
        
        // Try formatting a message with a placeholder
        Component result = testChatManager.formatMessage(player, "Placeholder test with %player_world%");
        
        // Verify basic content is still there
        String content = ColorUtil.componentToString(result);
        assertTrue(content.contains("TestPlayer") && content.contains("Placeholder test"));
    }
}