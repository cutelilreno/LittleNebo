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
import moe.reno.littlenebo.LittleNebo;
import moe.reno.littlenebo.util.ColorUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests for NeboChatRenderer class using MockBukkit
 */
public class NeboChatRendererTest {
    private ServerMock server;
    private LittleNebo plugin;
    private ChatManager chatManager;
    private NeboChatRenderer chatRenderer;
    private PlayerMock sourcePlayer;
    private PlayerMock viewerPlayer;

    @SuppressWarnings("deprecation")
    @BeforeEach
    public void setUp() {
        // Start a mock server
        server = MockBukkit.mock();
        
        // Create a mock plugin instance - Fixed for MockBukkit API
        plugin = Mockito.mock(LittleNebo.class);
        
        // Create a mock ChatManager that returns a simple formatted message
        chatManager = Mockito.mock(ChatManager.class);
        when(plugin.getChatManager()).thenReturn(chatManager);
        
        // Create the real chat renderer to test
        chatRenderer = new NeboChatRenderer(plugin);
        
        // Create test players
        sourcePlayer = server.addPlayer("SourcePlayer");
        sourcePlayer.setDisplayName("SourcePlayer");
        
        viewerPlayer = server.addPlayer("ViewerPlayer");
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void render_withStoredMessage_usesFormattedMessage() {
        // Setup: Store a message for the player
        String testMessage = "Hello, world!";
        chatRenderer.setLastMessage(sourcePlayer, testMessage);
        
        // Setup: Configure chat manager to return a specific component
        Component expectedComponent = Component.text("FORMATTED: " + testMessage);
        when(chatManager.formatMessage(sourcePlayer, testMessage)).thenReturn(expectedComponent);
        
        // Test the render method
        Component result = chatRenderer.render(
            sourcePlayer,
            Component.text("Unused display name"),
            Component.text("Unused message"),
            (Audience) viewerPlayer
        );
        
        // Verify the result is what we expected
        assertEquals(
            ColorUtil.componentToString(expectedComponent), 
            ColorUtil.componentToString(result)
        );
    }
    
    @Test
    public void render_withNoStoredMessage_usesEmptyString() {
        // Setup: Don't store any message (to test default behavior)
        
        // Setup: Configure chat manager to return a specific component for empty string
        Component expectedComponent = Component.text("FORMATTED: ");
        when(chatManager.formatMessage(sourcePlayer, "")).thenReturn(expectedComponent);
        
        // Test the render method
        Component result = chatRenderer.render(
            sourcePlayer,
            Component.text("Unused display name"),
            Component.text("Unused message"),
            (Audience) viewerPlayer
        );
        
        // Verify the result is what we expected
        assertEquals(
            ColorUtil.componentToString(expectedComponent), 
            ColorUtil.componentToString(result)
        );
    }
    
    @Test
    public void removeLastMessage_afterSetting_removesMessage() {
        // Setup: Store a message for the player
        String testMessage = "Test message";
        chatRenderer.setLastMessage(sourcePlayer, testMessage);
        
        // Configure chat manager mocks for both checks
        Component beforeComponent = Component.text("BEFORE: " + testMessage);
        Component afterComponent = Component.text("AFTER: ");  // empty string response
        when(chatManager.formatMessage(sourcePlayer, testMessage)).thenReturn(beforeComponent);
        when(chatManager.formatMessage(sourcePlayer, "")).thenReturn(afterComponent);
        
        // Test 1: Verify the message is set
        Component beforeResult = chatRenderer.render(
            sourcePlayer,
            Component.text(""),
            Component.text(""),
            (Audience) viewerPlayer
        );
        assertEquals(ColorUtil.componentToString(beforeComponent), ColorUtil.componentToString(beforeResult));
        
        // Remove the message
        chatRenderer.removeLastMessage(sourcePlayer);
        
        // Test 2: Verify the message was removed
        Component afterResult = chatRenderer.render(
            sourcePlayer,
            Component.text(""),
            Component.text(""),
            (Audience) viewerPlayer
        );
        assertEquals(ColorUtil.componentToString(afterComponent), ColorUtil.componentToString(afterResult));
    }
}