package moe.reno.littlenebo.chat;

import moe.reno.littlenebo.LittleNebo;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the NeboChatRenderer class.
 */
public class NeboChatRendererTest {

    @Mock
    private LittleNebo plugin;
    
    @Mock
    private ChatManager chatManager;
    
    @Mock
    private Player source;
    
    @Mock
    private Player viewer;
    
    @Mock
    private Audience consoleViewer;
    
    private NeboChatRenderer renderer;
    private UUID sourceUuid = UUID.randomUUID();
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up plugin mock
        when(plugin.getChatManager()).thenReturn(chatManager);
        when(plugin.getConfigManager()).thenReturn(mock(moe.reno.littlenebo.config.ConfigManager.class));
        
        // Set up player mocks
        when(source.getUniqueId()).thenReturn(sourceUuid);
        when(source.getName()).thenReturn("SourcePlayer");
        
        when(viewer.getName()).thenReturn("ViewerPlayer");
        
        // Set up chat manager mock
        when(chatManager.formatMessage(eq(source), anyString())).thenReturn(Component.text("Formatted message"));
        
        // Create the renderer
        renderer = new NeboChatRenderer(plugin);
    }
    
    /**
     * Test that render correctly formats a message for a player viewer.
     */
    @Test
    public void testRenderForPlayerViewer() {
        // Set up a message
        renderer.setLastMessage(source, "Test message");
        
        // Call the method to test
        Component result = renderer.render(source, Component.text("Display Name"), Component.text("Original Message"), viewer);
        
        // Verify the chat manager was called to format the message
        verify(chatManager).formatMessage(source, "Test message");
        
        // Verify the result is not null
        assertNotNull(result, "Rendered message should not be null");
        
        // Verify debug was called
        verify(plugin).debug(contains("Rendering chat message"));
    }
    
    /**
     * Test that render correctly formats a message for a console viewer.
     */
    @Test
    public void testRenderForConsoleViewer() {
        // Set up a message
        renderer.setLastMessage(source, "Test message");
        
        // Call the method to test
        Component result = renderer.render(source, Component.text("Display Name"), Component.text("Original Message"), consoleViewer);
        
        // Verify the chat manager was called to format the message
        verify(chatManager).formatMessage(source, "Test message");
        
        // Verify the result is not null
        assertNotNull(result, "Rendered message should not be null");
        
        // Verify debug was called with "Console" as the viewer
        verify(plugin).debug(contains("Console"));
    }
    
    /**
     * Test that render handles the case where no message was stored for the player.
     */
    @Test
    public void testRenderWithNoStoredMessage() {
        // Don't set a message for the player
        
        // Call the method to test
        Component result = renderer.render(source, Component.text("Display Name"), Component.text("Original Message"), viewer);
        
        // Verify the chat manager was called with an empty string
        verify(chatManager).formatMessage(source, "");
        
        // Verify the result is not null
        assertNotNull(result, "Rendered message should not be null");
    }
    
    /**
     * Test that setLastMessage correctly stores a message for a player.
     */
    @Test
    public void testSetLastMessage() {
        // Call the method to test
        renderer.setLastMessage(source, "Test message");
        
        // Verify the message was stored by rendering it
        Component result = renderer.render(source, Component.text("Display Name"), Component.text("Original Message"), viewer);
        
        // Verify the chat manager was called with the stored message
        verify(chatManager).formatMessage(source, "Test message");
    }
    
    /**
     * Test that removeLastMessage correctly removes a stored message for a player.
     */
    @Test
    public void testRemoveLastMessage() {
        // Set up a message
        renderer.setLastMessage(source, "Test message");
        
        // Call the method to test
        renderer.removeLastMessage(source);
        
        // Verify the message was removed by rendering
        Component result = renderer.render(source, Component.text("Display Name"), Component.text("Original Message"), viewer);
        
        // Verify the chat manager was called with an empty string
        verify(chatManager).formatMessage(source, "");
    }
}