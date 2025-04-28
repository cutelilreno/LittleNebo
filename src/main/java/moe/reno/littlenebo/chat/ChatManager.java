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

import static org.bukkit.Bukkit.getServer;

/**
 * Manages chat and formatting of player messages.
 * <p>
 *     Delegates rendering to {@link NeboChatRenderer}, which
 *     uses {@link ChatManager#formatMessage(Player, String)} to apply
 *     MiniMessage or legacy formatting based on the conf.
 * </p>
 */
public class ChatManager implements Listener {
    private final LittleNebo plugin;
    private final ConfigManager configManager;
    private final NeboChatRenderer chatRenderer;
    private final boolean placeholdersEnabled = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

    public ChatManager(LittleNebo plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.chatRenderer = new NeboChatRenderer(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        Component originalMessage = event.message();

        event.renderer(chatRenderer);

        String messageStr = ColorUtil.componentToString(originalMessage);
        chatRenderer.setLastMessage(player, messageStr);

        if(configManager.isDebugEnabled()) {
            plugin.debug("Chat event processed for " + player.getName());
            plugin.debug("Original message: " + ColorUtil.componentToString(originalMessage));
            plugin.debug("Processed message: " + messageStr);
        }
    }

    /**
     * Format a chat message.
     * <p>
     *     Chooses and applies a format based on player permissions and conf,
     * </p>
     *
     * @param player  the player who sent the chat
     * @param message the raw message text
     * @return a formatted {@link Component}
     */
    public Component formatMessage(Player player, String message) {
        FormatConfig format = configManager.getFormatForPlayer(player);

        final String displayName = player.getDisplayName();

        final Component processedMessage = configManager.isPlayerLegacyColorsEnabled()
                ? ColorUtil.parseMixedFormattingComponent(message)
                : ColorUtil.parseSafeMiniMessage(message);

        String formatTemplate = format.hasLegacyFormatConf()
                ? format.legacyFormat()
                : format.format();

        if(placeholdersEnabled) {
            formatTemplate = PlaceholderAPI.setPlaceholders(player, formatTemplate);
        }

        if (format.hasLegacyFormatConf()) {
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