package moe.reno.littlenebo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * ColorUtil Utility
 * <p>
 *     Various utilities to help use MiniMessage and legacy codes with Adventure.
 * </p>
 */
public class ColorUtil {

    /**
     * Unsafe MiniMessage parser
     * <p>
     *     Resolves all MiniMessage tags! Do not use on untrusted input!
     * </p>
     */
    private static final MiniMessage unsafeMiniMessage = MiniMessage.miniMessage();

    /**
     * Safe MiniMessage parser
     * <p>
     *     Only color, gradient, rainbow, pride, and decoration tags will resolve.
     *     Used for untrusted player input.
     * </p>
     */
    private static final MiniMessage safeMiniMessage = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.gradient())
                    .resolver(StandardTags.rainbow())
                    .resolver(StandardTags.pride())
                    .resolver(StandardTags.decorations()
                    )
                    .build()
            )
            .build();

    /**
     * Serializes a Component to its string without formatting.
     *
     * @param component the Component to convert; may be null
     * @return a String (or empty string if component was null)
     */
    public static String componentToString(Component component) {
        if (component == null) {
            return "";
        }
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Convert strings with legacy Minecraft color codes (using '{@literal &}' prefixes) into MiniMessage.
     *<p>
     * Example: {@code "&cHello &6World"} → {@code <red>Hello <gold>World</gold></red>}
     *</p>
     *
     * @param text a String containing '{@literal &}' legacy color codes
     * @return a formatted Component
     */
    public static Component legacyToComponent(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    /**
     * Parses a MiniMessage-formatted String into an Adventure Component.
     *<p>
     * Trusted content (for example, formats from config.yml) should use this method to ensure
     * all tags—including hover, click, and insertion—are supported.
     *</p>
     *
     * @param text a String containing MiniMessage tags
     * @return a formatted Component; never null
     */
    public static Component parseMiniMessage(String text) {
        return  unsafeMiniMessage.deserialize(text);
    }

    /**
     * Mixed Formatting parser
     *
     * For processing mixed legacy ('{@literal &}') codes and MiniMessage tags together.
     * Legacy codes are converted to MiniMessage so the entire String is only MiniMessage,
     * then put through the safe parser.
     * <p>
     *     Use this for untrusted input (like player chat) to allow styling without interactive tags.
     * </p>
     *
     * @param text a String containing both '{@literal &}' codes and MiniMessage tags
     * @return a formatted Component, restricted to safe styling; never null
     */
    public static Component parseMixedFormattingComponent(String text) {
        // im just going to live with this
        text = text
                .replaceAll("(?i)&#([0-9a-f]{6})", "<#$1>")
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&l", "<bold>")
                .replace("&o", "<italic>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");
        return safeMiniMessage.deserialize(text);
    }

    /**
     * Parses a MiniMessage String with only safe styling tags (color, gradient, rainbow, pride, decorations).
     *<p>
     * Should be used only for untrusted input. Does not process legacy codes.
     *</p>
     *
     * @param text a String containing MiniMessage styling tags
     * @return a formatted Component with only safe tags; never null
     */
    public static Component parseSafeMiniMessage(String text) {
        return safeMiniMessage.deserialize(text);
    }

}

