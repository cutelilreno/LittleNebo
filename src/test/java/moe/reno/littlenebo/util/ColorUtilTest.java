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

        // Test with null input
        Component nullComponent = ColorUtil.parseMixedFormattingComponent(null);
        assertEquals("", ColorUtil.componentToString(nullComponent),
                "Null input should return empty component");

        // Test with hex color code
        String hexText = "&#ff0000Red";
        Component hexComponent = ColorUtil.parseMixedFormattingComponent(hexText);
        assertEquals("Red", ColorUtil.componentToString(hexComponent),
                "Hex color code should be processed correctly");
    }

    /**
     * Test that parseMiniMessage correctly parses MiniMessage formatting.
     */
    @Test
    public void testParseMiniMessage() {
        // Test basic MiniMessage parsing
        String miniText = "<red>Red</red> <blue>Blue</blue>";
        Component component = ColorUtil.parseMiniMessage(miniText);

        // Verify the text content
        assertEquals("Red Blue", ColorUtil.componentToString(component),
                "MiniMessage text content should be preserved");

        // Test with null input (should not throw exception)
        Component nullComponent = ColorUtil.parseMiniMessage(null);
        assertNotNull(nullComponent, "Result should not be null even with null input");
    }

    /**
     * Test that serialiseMiniMessage correctly serializes a component to MiniMessage format.
     */
    @Test
    public void testSerialiseMiniMessage() {
        // Create a component with formatting
        Component component = ColorUtil.parseMiniMessage("<red>Test</red>");

        // Serialize it back to MiniMessage format
        String serialized = ColorUtil.serialiseMiniMessage(component);

        // Verify it contains MiniMessage tags (exact format may vary)
        assertTrue(serialized.contains("<red>") || serialized.contains("color=red"),
                "Serialized text should contain color information");
        assertTrue(serialized.contains("Test"),
                "Serialized text should contain the original content");
    }

    /**
     * Test that prideTagToGradient correctly converts pride tags to gradients.
     */
    @Test
    public void testPrideTagToGradient() {
        // Test with null input
        assertEquals("", ColorUtil.prideTagToGradient(null),
                "Null input should return empty string");

        // Test with generic pride tag
        String prideText = "<pride>Rainbow</pride>";
        String result = ColorUtil.prideTagToGradient(prideText);
        assertTrue(result.contains("<gradient:"), "Pride tag should be converted to gradient");
        assertTrue(result.contains("</gradient>"), "Pride closing tag should be converted");

        // Test with specific pride tag
        String specificPrideText = "<pride:trans>Trans Pride</pride>";
        String specificResult = ColorUtil.prideTagToGradient(specificPrideText);
        assertTrue(specificResult.contains("<gradient:"), "Specific pride tag should be converted to gradient");
        assertTrue(specificResult.contains("#5bcffb"), "Trans pride colors should be included");
    }

    /**
     * Test that parseSafeMiniMessage correctly parses safe MiniMessage tags.
     */
    @Test
    public void testParseSafeMiniMessage() {
        // Test with null input
        Component nullComponent = ColorUtil.parseSafeMiniMessage(null);
        assertEquals("", ColorUtil.componentToString(nullComponent),
                "Null input should return empty component");

        // Test with safe tags
        String safeText = "<red>Red</red> <blue>Blue</blue>";
        Component safeComponent = ColorUtil.parseSafeMiniMessage(safeText);
        assertEquals("Red Blue", ColorUtil.componentToString(safeComponent),
                "Safe tags should be parsed correctly");
    }

    /**
     * Test the deprecated componentToStringWithPride method.
     */
    @Test
    public void testComponentToStringWithPride() {
        // Test with null component
        assertEquals("", ColorUtil.componentToStringWithPride(null),
                "Null component should return empty string");

        // Test with a simple component
        Component component = Component.text("Hello <pride>Rainbow</pride>");
        String result = ColorUtil.componentToStringWithPride(component);
        assertTrue(result.contains("Hello"), "Original text should be preserved");
        assertTrue(result.contains("<gradient:"), "Pride tag should be converted to gradient");
    }
}
