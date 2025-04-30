package moe.reno.littlenebo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Map;

/**
 * ColorUtil Utility
 * <p>
 *     Various utilities to help use MiniMessage and legacy codes with Adventure.
 * </p>
 */
public class ColorUtil {
    private static final Map<String, String> PRIDE_TAGS = Map.ofEntries(
            Map.entry("pride", "#e50000:#ff8d00:#ffee00:#028121:#004cff:#770088"),
            Map.entry("progress", "white:#ffafc7:#73d7ee:#613915:black:#e50000:#ff8d00:#ffee00:#028121:#004cff:#770088"),
            Map.entry("trans", "#5bcffb:#f5abb9:white:#f5abb9:#5bcffb"),
            Map.entry("bi", "#d60270:#9b4f96:#0038a8"),
            Map.entry("pan", "#ff1c8d:#ffd700:#1ab3ff"),
            Map.entry("nb", "#fcf431:#fcfcfc:#9d59d2:#282828"),
            Map.entry("lesbian", "#d62800:#ff9b56:white:#4662a6:#a40062"),
            Map.entry("ace", "black:#a4a4a4:white:#810081"),
            Map.entry("agender", "black:#bababa:white:#baf484:white:#bababa:black"),
            Map.entry("demisexual", "black:white:#6e0071:#d3d3d3"),
            Map.entry("genderqueer", "#b57fdd:white:#49821e"),
            Map.entry("genderfluid", "#fe76a2:white:#bf12d7:black:#303cbe"),
            Map.entry("intersex", "#ffd800:#7902aa:#ffd800"),
            Map.entry("aro", "#3ba740:#a8d47a:white:#ababab:black"),
            Map.entry("baker", "#cd66ff:#ff6599:#fe0000:#fe9900:#ffff01:#009900:#0099cb:#350099:#990099"),
            Map.entry("philly", "black:#784f17:#fe0000:#fd8c00:#ffe500:#119f0b:#0644b3:#c22edc"),
            Map.entry("queer", "black:#9ad9ea:#00a3e8:#b5e51d:white:#ffc90d:#fc6667:#feaec9:black"),
            Map.entry("gay", "#078e70:#26ceaa:#98e8c1:white:#7bade2:#5049cb:#3d1a78"),
            Map.entry("bigender", "#c479a0:#eca6cb:#d5c7e8:white:#d5c7e8:#9ac7e8:#6c83cf"),
            Map.entry("demigender", "#7f7f7f:#c3c3c3:#fbff74:white:#fbff74:#c3c3c3:#7f7f7f")
    );
    private static final Map<String, String> LEGACY_CODES = Map.ofEntries(
            Map.entry("&0", "<black>"),
            Map.entry("&1", "<dark_blue>"),
            Map.entry("&2", "<dark_green>"),
            Map.entry("&3", "<dark_aqua>"),
            Map.entry("&4", "<dark_red>"),
            Map.entry("&5", "<dark_purple>"),
            Map.entry("&6", "<gold>"),
            Map.entry("&7", "<gray>"),
            Map.entry("&8", "<dark_gray>"),
            Map.entry("&9", "<blue>"),
            Map.entry("&a", "<green>"),
            Map.entry("&b", "<aqua>"),
            Map.entry("&c", "<red>"),
            Map.entry("&d", "<light_purple>"),
            Map.entry("&e", "<yellow>"),
            Map.entry("&f", "<white>"),
            Map.entry("&l", "<bold>"),
            Map.entry("&o", "<italic>"),
            Map.entry("&n", "<underlined>"),
            Map.entry("&m", "<strikethrough>"),
            Map.entry("&k", "<obfuscated>"),
            Map.entry("&r", "<reset>")
    );

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
                    .resolver(StandardTags.reset())
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
     * @deprecated use {@link #componentToString(Component)} and process pride separately
     * Serializes a Component to its string without formatting.
     *
     * @param component the Component to convert; may be null
     * @return a String (or empty string if component was null)
     */
    @Deprecated
    public static String componentToStringWithPride(Component component) {
        if (component == null) {
            return "";
        }
        String serialized = PlainTextComponentSerializer.plainText().serialize(component);
        serialized = prideTagToGradient(serialized);
        return serialized;
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
        if (text == null) {
            return Component.empty();
        }
        return unsafeMiniMessage.deserialize(text);
    }

    /**
     * Serializes a MiniMessage Component into its string with formatting.
     *
     * @param component a MiniMessage component
     * @return a formatted string
     */
    public static String serialiseMiniMessage(Component component) {
        return unsafeMiniMessage.serialize(component);
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
        if (text == null) {
            return Component.empty();
        }
        text = text.replaceAll("(?i)&#([0-9a-f]{6})", "<#$1>");
        for (Map.Entry<String, String> entry : LEGACY_CODES.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return safeMiniMessage.deserialize(text);
    }

    /**
     * Convert strings with {@literal <pride>} into {@literal <gradient>}.
     *<p>
     * Example: {@code "<pride:genderqueer>"} → {@code <gradient:b57fdd:white:#49821e>}
     *</p>
     *
     * @param text a String
     * @return a String
     */
    public static String prideTagToGradient(String text) {
        if (text == null) {
            return "";
        }
        for (Map.Entry<String, String> entry : PRIDE_TAGS.entrySet()) {
            text = text.replace("<pride:" + entry.getKey() + ">", "<gradient:" + entry.getValue() + ">");
        }
        text = text
                .replace("<pride>", "<gradient:" + PRIDE_TAGS.getOrDefault("pride", "#e50000:#ff8d00:#ffee00:#028121:#004cff:#770088") + ">")
                .replace("</pride>", "</gradient>")
        ;
        return text;
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
        if (text == null) {
            return Component.empty();
        }
        return safeMiniMessage.deserialize(text);
    }

}
