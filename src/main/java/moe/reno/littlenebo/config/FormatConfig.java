package moe.reno.littlenebo.config;

/**
 * Handles format settings. These are loaded from the plugin conf.
 */
public record FormatConfig (String format, String legacyFormat, String groupPermission) {

    /**
     * Checks if legacy format is configured for chat.
     *
     * @return true if legacy codes are enabled
     */
    public boolean hasLegacyFormatConf() {
        return legacyFormat != null && !legacyFormat.isEmpty();
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