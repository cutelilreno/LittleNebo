package moe.reno.littlenebo.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the FormatConfig record.
 */
public class FormatConfigTest {

    /**
     * Test that the FormatConfig constructor correctly sets all fields.
     */
    @Test
    public void testConstructor() {
        // Create a FormatConfig with all fields
        FormatConfig config = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                "&7{display_name} &f{message}",
                "littlenebo.vip"
        );
        
        // Verify all fields were set correctly
        assertEquals("<gray>{display_name}</gray> <white>{message}</white>", config.format(),
                "Format should be set correctly");
        assertEquals("&7{display_name} &f{message}", config.legacyFormat(),
                "Legacy format should be set correctly");
        assertEquals("littlenebo.vip", config.groupPermission(),
                "Group permission should be set correctly");
    }
    
    /**
     * Test that hasLegacyFormatConf correctly identifies when legacy format is configured.
     */
    @Test
    public void testHasLegacyFormatConf() {
        // Create a FormatConfig with legacy format
        FormatConfig configWithLegacy = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                "&7{display_name} &f{message}",
                "littlenebo.vip"
        );
        
        // Verify it has legacy format
        assertTrue(configWithLegacy.hasLegacyFormatConf(),
                "Should report having legacy format when legacy format is set");
        
        // Create a FormatConfig without legacy format
        FormatConfig configWithoutLegacy = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                "",
                "littlenebo.vip"
        );
        
        // Verify it doesn't have legacy format
        assertFalse(configWithoutLegacy.hasLegacyFormatConf(),
                "Should report not having legacy format when legacy format is empty");
        
        // Create a FormatConfig with null legacy format
        FormatConfig configWithNullLegacy = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                null,
                "littlenebo.vip"
        );
        
        // Verify it doesn't have legacy format
        assertFalse(configWithNullLegacy.hasLegacyFormatConf(),
                "Should report not having legacy format when legacy format is null");
    }
    
    /**
     * Test that hasPermission correctly identifies when permission is configured.
     */
    @Test
    public void testHasPermission() {
        // Create a FormatConfig with permission
        FormatConfig configWithPermission = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                "&7{display_name} &f{message}",
                "littlenebo.vip"
        );
        
        // Verify it has permission
        assertTrue(configWithPermission.hasPermission(),
                "Should report having permission when permission is set");
        
        // Create a FormatConfig without permission
        FormatConfig configWithoutPermission = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                "&7{display_name} &f{message}",
                ""
        );
        
        // Verify it doesn't have permission
        assertFalse(configWithoutPermission.hasPermission(),
                "Should report not having permission when permission is empty");
        
        // Create a FormatConfig with null permission
        FormatConfig configWithNullPermission = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                "&7{display_name} &f{message}",
                null
        );
        
        // Verify it doesn't have permission
        assertFalse(configWithNullPermission.hasPermission(),
                "Should report not having permission when permission is null");
    }
    
    /**
     * Test that the record's equals and hashCode methods work correctly.
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical FormatConfigs
        FormatConfig config1 = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                "&7{display_name} &f{message}",
                "littlenebo.vip"
        );
        
        FormatConfig config2 = new FormatConfig(
                "<gray>{display_name}</gray> <white>{message}</white>",
                "&7{display_name} &f{message}",
                "littlenebo.vip"
        );
        
        // Verify they are equal and have the same hash code
        assertEquals(config1, config2, "Identical FormatConfigs should be equal");
        assertEquals(config1.hashCode(), config2.hashCode(), "Identical FormatConfigs should have the same hash code");
        
        // Create a different FormatConfig
        FormatConfig config3 = new FormatConfig(
                "<gold>{display_name}</gold> <yellow>{message}</yellow>",
                "&6{display_name} &e{message}",
                "littlenebo.admin"
        );
        
        // Verify they are not equal
        assertNotEquals(config1, config3, "Different FormatConfigs should not be equal");
    }
}