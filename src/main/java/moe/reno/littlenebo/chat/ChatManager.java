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
    private final boolean placeholdersEnabled;

    public ChatManager(LittleNebo plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;

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

    /**
     * Event handler for when a player sends a chat message.
     *
     * @param event the AsyncChatEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Component originalMessage = event.message();
        
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            // Work with the original message component directly
            Component processedMessage = processMessage(originalMessage);
            return formatWithComponent(source, processedMessage);
        });
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
    private Component formatWithComponent(Player player, Component message) {
        FormatConfig format = configManager.getFormatForPlayer(player);
        String formatTemplate = format.format();
        
        if (placeholdersEnabled && player != null) {
            formatTemplate = PlaceholderAPI.setPlaceholders(player, formatTemplate);
        }
        
        Component baseFormat = ColorUtil.parseMiniMessage(formatTemplate);
        
        return baseFormat
            .replaceText(builder -> builder
                .matchLiteral("{display_name}")
                .replacement(player.displayName()))
            .replaceText(builder -> builder
                .matchLiteral("{message}")
                .replacement(message));  // Use the Component directly
    }
 
    /**
     * Event handler for when a player sends a chat message.
     *
     * @param originalMessage the original message Component
     */
    private Component processMessage(Component originalMessage) {
        String messageStr = ColorUtil.componentToString(originalMessage);
        messageStr = ColorUtil.prideTagToGradient(messageStr);
        
        if (configManager.isPlayerLegacyColorsEnabled()) {
            // Parse both legacy (&) codes and safe MiniMessage tags
            return ColorUtil.parseMixedFormattingComponent(messageStr);
        } else {
            // Parse only safe MiniMessage tags
            return ColorUtil.parseSafeMiniMessage(messageStr);
        }
    }
}