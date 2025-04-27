package moe.reno.littlenebo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Utility class for converting between legacy Minecraft color codes, MiniMessage-formatted text,
 * and Adventure Components. Provides both full-featured parsing (for trusted sources) and
 * a restricted safe parser (for untrusted player input).
 */
public class ColorUtil {
    /**
     * The full MiniMessage parser, with all standard tags enabled (colors, gradients, hover, click, etc.).
     */
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * The safe MiniMessage parser, restricted to only color, gradient, rainbow, pride, and decoration tags.
     * Used for parsing untrusted player input to avoid click/hover exploits.
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
     * Serializes a Component to its plain-text representation, stripping out all formatting.
     *
     * @param component the Component to convert; may be null
     * @return a plain String (never null); empty if the component was null
     */
    public static String componentToString(Component component) {
        if (component == null) {
            return "";
        }
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Parses legacy Minecraft color codes (using '{@literal &}' prefixes) into an Adventure Component.
     *<p>
     * Example: {@code "&cHello &6World"} → {@code <red>Hello <gold>World</gold></red>}
     *</p>
     *
     * @param text a String containing '{@literal &}' legacy color codes
     * @return a formatted Component; never null
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
        return miniMessage.deserialize(text);
    }

    /**
     * Parses text that may contain mixed legacy ('{@literal &}') codes and MiniMessage tags,
     * converting '{@literal &}' codes into MiniMessage equivalent tags, then parsing with the safe parser.
     *<p>
     * Use this for untrusted input (e.g. player chat) to allow styling while preventing interactive tags.
     *</p>
     *
     * @param text a String containing both '{@literal &}' codes and MiniMessage tags
     * @return a formatted Component, restricted to safe styling; never null
     */
    public static Component parseMixedFormattingComponent(String text) {
        text = text
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
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
     * Should be used for untrusted input that should not allow hover/click interactions.
     *</p>
     *
     * @param text a String containing MiniMessage styling tags
     * @return a formatted Component with only safe tags; never null
     */
    public static Component parseSafeMiniMessage(String text) {
        return safeMiniMessage.deserialize(text);
    }

}

