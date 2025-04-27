package moe.reno.littlenebo.chat;

import moe.reno.littlenebo.LittleNebo;
import moe.reno.littlenebo.config.ConfigManager;
import moe.reno.littlenebo.config.FormatConfig;
import moe.reno.littlenebo.util.ColorUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Manages interception and formatting of player chat messages.
 * <p>
 * Listens for the Paper AsyncChatEvent, captures raw player input,
 * and delegates rendering to {@link NeboChatRenderer}, which in turn
 * uses {@link ChatManager#formatMessage(Player, String)} to apply
 * MiniMessage or legacy formatting based on the plugin configuration.
 * </p>
 */
public class ChatManager implements Listener {
    private final LittleNebo plugin;
    private final ConfigManager configManager;
    private final NeboChatRenderer chatRenderer;

    /**
     * Constructs a ChatManager and registers a custom chat renderer.
     *
     * @param plugin        main plugin instance for access to logger and config
     * @param configManager central config manager containing chat settings and formats
     */
    public ChatManager(LittleNebo plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.chatRenderer = new NeboChatRenderer(plugin);
    }

    /**
     * Handles incoming chat events at high priority.
     * <p>
     * Converts the incoming Component message to plain text,
     * stores it for rendering, and assigns a custom renderer
     * so that other plugins still receive the original event.
     * </p>
     *
     * @param event the async chat event containing the player's message
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // Get the message as string
        Component originalMessage = event.message();
        String messageStr = ColorUtil.componentToString(originalMessage);

        // IMPORTANT: We do not cancel the event, but we set a renderer
        // This allows other plugins to still receive and process the event
        event.renderer(chatRenderer);

        // Store the processed message in the renderer
        chatRenderer.setLastMessage(player, messageStr);

        // Debug information
        plugin.debug("Chat event processed for " + player.getName());
        plugin.debug("Original message: " + ColorUtil.componentToString(originalMessage));
        plugin.debug("Processed message: " + messageStr);
    }

    /**
     * Formats a chat message for display to a viewer.
     * <p>
     * Chooses a {@link FormatConfig} based on player permissions,
     * then applies either legacy '{@literal &}' codes or MiniMessage templates.
     * PlaceholderAPI placeholders are resolved in names and templates.
     * </p>
     *
     * @param player  the player who sent the chat (used for permissions/placeholders)
     * @param message the raw message text (no formatting)
     * @return a fully formatted {@link Component} ready to send
     */
    public Component formatMessage(Player player, String message) {
        FormatConfig format = configManager.getFormatForPlayer(player);

        final String displayName = PlaceholderAPI.setPlaceholders(player, player.getDisplayName());

        final Component processedMessage = configManager.isParsePlayerColors()
                ? ColorUtil.parseMixedFormattingComponent(message)
                : ColorUtil.parseSafeMiniMessage(message);

        String formatTemplate = format.hasLegacyFormat()
                ? format.getLegacyFormat()
                : format.getFormat();

        // Apply PlaceholderAPI to the template (no check needed anymore)
        formatTemplate = PlaceholderAPI.setPlaceholders(player, formatTemplate);

        if (format.hasLegacyFormat()) {
            String legacyFormatted = formatTemplate
                    .replace("{display_name}", displayName)
                    .replace("{message}", PlainTextComponentSerializer.plainText().serialize(processedMessage));

            return ColorUtil.legacyToComponent(legacyFormatted);
        } else {
            Component baseFormat = ColorUtil.parseMiniMessage(formatTemplate);

            return baseFormat
                    .replaceText(builder -> builder
                            .matchLiteral("{display_name}")
                            .replacement(Component.text(displayName)))
                    .replaceText(builder -> builder
                            .matchLiteral("{message}")
                            .replacement(processedMessage));
        }
    }
}