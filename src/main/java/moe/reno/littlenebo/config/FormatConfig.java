package moe.reno.littlenebo.config;

/**
 * Represents a single chat format definition, as loaded from the plugin configuration.
 * <p>
 * Each format may define either a MiniMessage-based format string or a legacy ampersand
 * ('{@literal &}') color code string, along with an optional permission node to restrict its use.
 * </p>
 */
public class FormatConfig {
    private final String format;
    private final String legacyFormat;
    private final String permission;

    /**
     * Constructs a new FormatConfig entry.
     *
     * @param format the MiniMessage format string (e.g. <code></code><gray>[<green>Chat</green>]</gray> <white>&#123;message&#125;</white></code>)
     * @param legacyFormat the legacy-style format string using '{@literal &}' codes (e.g. <code>&7[&aChat&7] &f{message}</code>), or empty/null if unused
     * @param permission the permission node required to use this format, or empty/null if unrestricted
     */
    public FormatConfig(String format, String legacyFormat, String permission) {
        this.format = format;
        this.legacyFormat = legacyFormat;
        this.permission = permission;
    }

    /**
     * Gets the MiniMessage-compatible format string.
     * <p>
     * This string may contain MiniMessage tags and the placeholders
     * <code>{display_name}</code> and <code>{message}</code>.
     * </p>
     *
     * @return the configured MiniMessage format (never null)
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets the legacy ampersand-based format string.
     * <p>
     * This string may contain '{@literal &}' color codes and the placeholder
     *     <code>{display_name}</code> and <code>{message}</code>.
     * </p>
     *
     * @return the configured legacy format, or empty/null if none
     */
    public String getLegacyFormat() {
        return legacyFormat;
    }

    /**
     * Checks if a non-empty legacy format is configured.
     *
     * @return true if <code>legacyFormat</code> is non-null and not empty
     */
    public boolean hasLegacyFormat() {
        return legacyFormat != null && !legacyFormat.isEmpty();
    }

    /**
     * Gets the permission node required for this format.
     *
     * @return the required permission, or empty/null if unrestricted
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Checks if this format is permission-gated.
     *
     * @return true if <code>permission</code> is non-null and not empty
     */
    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }
}