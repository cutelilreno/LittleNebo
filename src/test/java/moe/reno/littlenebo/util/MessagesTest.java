package moe.reno.littlenebo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Messages utility class.
 */
public class MessagesTest {

    /**
     * Test that error creates a red-colored message.
     */
    @Test
    public void testError() {
        // Create an error message
        Component message = Messages.error("Error message");
        
        // Verify it's not null
        assertNotNull(message, "Error message should not be null");
        
        // Verify it has the correct color
        assertEquals(NamedTextColor.RED, message.color(), "Error message should be red");
        
        // Verify it has the correct content
        assertEquals("Error message", ColorUtil.componentToString(message), 
                "Error message should have the correct text");
    }
    
    /**
     * Test that success creates a green-colored message.
     */
    @Test
    public void testSuccess() {
        // Create a success message
        Component message = Messages.success("Success message");
        
        // Verify it's not null
        assertNotNull(message, "Success message should not be null");
        
        // Verify it has the correct color
        assertEquals(NamedTextColor.GREEN, message.color(), "Success message should be green");
        
        // Verify it has the correct content
        assertEquals("Success message", ColorUtil.componentToString(message), 
                "Success message should have the correct text");
    }
    
    /**
     * Test that title creates a gold-colored message.
     */
    @Test
    public void testTitle() {
        // Create a title message
        Component message = Messages.title("Title message");
        
        // Verify it's not null
        assertNotNull(message, "Title message should not be null");
        
        // Verify it has the correct color
        assertEquals(NamedTextColor.GOLD, message.color(), "Title message should be gold");
        
        // Verify it has the correct content
        assertEquals("Title message", ColorUtil.componentToString(message), 
                "Title message should have the correct text");
    }
    
    /**
     * Test that info creates a gray-colored message.
     */
    @Test
    public void testInfo() {
        // Create an info message
        Component message = Messages.info("Info message");
        
        // Verify it's not null
        assertNotNull(message, "Info message should not be null");
        
        // Verify it has the correct color
        assertEquals(NamedTextColor.GRAY, message.color(), "Info message should be gray");
        
        // Verify it has the correct content
        assertEquals("Info message", ColorUtil.componentToString(message), 
                "Info message should have the correct text");
    }
    
    /**
     * Test that all message types handle empty strings correctly.
     */
    @Test
    public void testEmptyMessages() {
        // Create empty messages of each type
        Component errorMessage = Messages.error("");
        Component successMessage = Messages.success("");
        Component titleMessage = Messages.title("");
        Component infoMessage = Messages.info("");
        
        // Verify they're all not null
        assertNotNull(errorMessage, "Empty error message should not be null");
        assertNotNull(successMessage, "Empty success message should not be null");
        assertNotNull(titleMessage, "Empty title message should not be null");
        assertNotNull(infoMessage, "Empty info message should not be null");
        
        // Verify they all have empty content
        assertEquals("", ColorUtil.componentToString(errorMessage), 
                "Empty error message should have empty text");
        assertEquals("", ColorUtil.componentToString(successMessage), 
                "Empty success message should have empty text");
        assertEquals("", ColorUtil.componentToString(titleMessage), 
                "Empty title message should have empty text");
        assertEquals("", ColorUtil.componentToString(infoMessage), 
                "Empty info message should have empty text");
    }
}