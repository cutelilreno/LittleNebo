/**
 * MIT License
 * Copyright (c) 2025 cutelilreno
 * https://opensource.org/licenses/MIT
 */
package moe.reno.littlenebo.config;

/**
 * Handles format settings. These are loaded from the plugin conf.
 */
public record FormatConfig (String format, String legacyFormat, String groupPermission) {

    /**
     * Creates a validated FormatConfig with fallback to default if invalid.
     */
    public static FormatConfig createValidated(
            String format, 
            String legacyFormat, 
            String groupPermission) {
            
        // Do not check for {display_name} in case they use PlaceholderAPI
        if (format == null || !format.contains("{message}")) {
            return new FormatConfig("{display_name}: {message}", "", "");
        }
        legacyFormat = ""; // remove as it is deprecated
        return new FormatConfig(format, legacyFormat, groupPermission);
    }

    /**
     * Checks if this format is permission-gated.
     *
     * @return true if they have perms
     */
    public boolean hasPermission() {
        return groupPermission != null && !groupPermission.isEmpty();
    }
}