package moe.reno.littlenebo.util;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ColorUtil class.
 */
public class ColorUtilTest {

    /**
     * Test that componentToString correctly converts a Component to a plain string.
     */
    @Test
    public void testComponentToString() {
        // Test with null component
        assertEquals("", ColorUtil.componentToString(null),
                "Null component should return empty string");
        
        // Test with a simple component
        Component component = Component.text("Hello World");
        assertEquals("Hello World", ColorUtil.componentToString(component),
                "Component text should be extracted correctly");
    }
    
    /**
     * Test that legacyToComponent correctly converts legacy color codes.
     */
    @Test
    public void testLegacyToComponent() {
        // Test basic legacy color conversion
        String legacyText = "&cRed &6Gold";
        Component component = ColorUtil.legacyToComponent(legacyText);
        
        // We can't easily test the color directly, but we can verify the text content
        assertEquals("Red Gold", ColorUtil.componentToString(component),
                "Legacy text content should be preserved");
    }
    
    /**
     * Test that parseMixedFormattingComponent correctly handles mixed formatting.
     */
    @Test
    public void testParseMixedFormattingComponent() {
        // Test mixed formatting
        String mixedText = "&cRed <blue>Blue</blue>";
        Component component = ColorUtil.parseMixedFormattingComponent(mixedText);
        
        // Verify the text content
        assertEquals("Red Blue", ColorUtil.componentToString(component),
                "Mixed formatting text content should be preserved");
    }
}