/**
 * MIT License
 * Copyright (c) 2025 cutelilreno
 * https://opensource.org/licenses/MIT
 */
package moe.reno.littlenebo.chat;

import moe.reno.littlenebo.LittleNebo;
import moe.reno.littlenebo.config.ConfigManager;
import moe.reno.littlenebo.config.FormatConfig;
import moe.reno.littlenebo.util.ColorUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
    private final boolean placeholdersEnabled;

    public ChatManager(LittleNebo plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.chatRenderer = new NeboChatRenderer(plugin);

        // Safely check if PlaceholderAPI is enabled
        boolean placeholdersAvailable = false;
        try {
            if (getServer() != null && getServer().getPluginManager() != null) { // always true in production, but not in tests
                placeholdersAvailable = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking for PlaceholderAPI: " + e.getMessage());
        }
        this.placeholdersEnabled = placeholdersAvailable;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        Component originalMessage = event.message();

        event.renderer(chatRenderer);

        String messageStr = ColorUtil.componentToString(originalMessage);
        messageStr = ColorUtil.prideTagToGradient(messageStr);
        chatRenderer.setLastMessage(player, messageStr);

        if(configManager.isDebugEnabled()) {
            plugin.debug("Chat event processed for " + player.getName());
            plugin.debug("Original message: " + ColorUtil.componentToString(originalMessage));

            if(configManager.isPlayerLegacyColorsEnabled()) {
                plugin.debug("Processed message: " + ColorUtil.serialiseMiniMessage(ColorUtil.parseMixedFormattingComponent(messageStr)));
            } else plugin.debug("Processed message: " + messageStr);

        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Honestly unless you have an insane influx of players joining and leaving,
        // this probably isn't needed. But it's here for completeness.
        chatRenderer.removeLastMessage(event.getPlayer());
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
        final String displayName = player != null ? 
            ColorUtil.componentToString(player.displayName()) : "Console";
       
        FormatConfig format = configManager.getFormatForPlayer(player);
        
        final Component processedMessage = configManager.isPlayerLegacyColorsEnabled()
                ? ColorUtil.parseMixedFormattingComponent(message)
                : ColorUtil.parseSafeMiniMessage(message);

        String formatTemplate = format.format();

        if(placeholdersEnabled && player != null) {
            formatTemplate = PlaceholderAPI.setPlaceholders(player, formatTemplate);
        }

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
