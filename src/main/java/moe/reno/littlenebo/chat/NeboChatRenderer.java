/**
 * MIT License
 *
 * Copyright (c) 2025 cutelilreno
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package moe.reno.littlenebo.chat;

import moe.reno.littlenebo.LittleNebo;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A custom chat renderer for Paper that delegates chat formatting to the plugin's ChatManager.
 * <p>
 * Stores the most recent raw player input message per-player, then uses that stored string
 * to generate a fully formatted {@link Component} for each viewer via {@link LittleNebo#getChatManager()}.
 * </p>
 */
public class NeboChatRenderer implements ChatRenderer {
    private final LittleNebo plugin;
    private final Map<UUID, String> lastMessages = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 1000;

    /**
     * Constructs a new NeboChatRenderer bound to the main plugin instance.
     *
     * @param plugin the LittleNebo plugin, used to access configuration and ChatManager
     */
    public NeboChatRenderer(LittleNebo plugin) {
        this.plugin = plugin;
    }

    /**
     * Renders a chat message for a given viewer.
     * <p>
     * Looks up the previously stored raw message for the source player and passes it to
     * {@code plugin.getChatManager().formatMessage(...)} to produce the final message component.
     * Also logs debug information if debug mode is enabled.
     * </p>
     *
     * @param source the player who sent the chat message
     * @param sourceDisplayName the original display name component (unused by this renderer)
     * @param message the original message component (unused by this renderer)
     * @param viewer the audience (player or console) receiving the message
     * @return a formatted {@link Component} to be shown to the viewer
     */
    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        // Get the message string from our stored messages
        String messageStr = lastMessages.getOrDefault(source.getUniqueId(), "");

        // Format the message using our chat manager
        Component formatted = plugin.getChatManager().formatMessage(source, messageStr);

        // Debug info
        plugin.debug("Rendering chat message for " + source.getName() + " to viewer " +
                (viewer instanceof Player ? ((Player) viewer).getName() : "Console"));

        return formatted;
    }

    /**
     * Stores the raw text of a player's message to be used by {@link #render}.
     * <p>
     * This should be called when the AsyncChatEvent is processed, before setting the renderer.
     * </p>
     *
     * @param player the player who sent the message
     * @param message the raw plain-text message without formatting applied
     */
    public void setLastMessage(Player player, String message) {
        if (lastMessages.size() > MAX_CACHE_SIZE) {
            Iterator<Map.Entry<UUID, String>> it = lastMessages.entrySet().iterator();
            for (int i = 0; i < 100 && it.hasNext(); i++) {
                it.next();
                it.remove();
            }
        }
    }
    public void removeLastMessage(Player player) {
        lastMessages.remove(player.getUniqueId());
    }
}