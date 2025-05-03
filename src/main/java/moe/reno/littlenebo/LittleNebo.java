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
package moe.reno.littlenebo;

import moe.reno.littlenebo.chat.ChatManager;
import moe.reno.littlenebo.commands.LittleNeboCommand;
import moe.reno.littlenebo.config.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Little Nebo.
 * <p>
 * Handles enabling/disabling the plugin
 * </p>
 */
public class LittleNebo extends JavaPlugin {
    private ConfigManager configManager;
    private ChatManager chatManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        chatManager = new ChatManager(this, configManager);
        String version = Bukkit.getBukkitVersion();
        if (version.startsWith("1.19.0") || !version.matches("^(1\\.(19|2\\d|[3-9]\\d)|([2-9]\\d+)\\.\\d+).*")) {
            getLogger().severe("╔══ Version Error ═══════════════════════");
            getLogger().severe("║ LittleNebo requires Paper 1.19.1 or higher!");
            getLogger().severe("║ Detected version: " + version);
            getLogger().severe("║ Plugin will not be enabled.");
            getLogger().severe("╚════════════════════════════════════════");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!version.matches("^1\\.(19|20|21)(\\.|$).*")) {
            getLogger().warning("╔══ Version Warning ══════════════════════");
            getLogger().warning("║ LittleNebo is running on an untested Minecraft version!");
            getLogger().warning("║ Detected version: " + version);
            getLogger().warning("║ The plugin may not work correctly.");
            getLogger().warning("╚════════════════════════════════════════");
        }
        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
        } catch (ClassNotFoundException e) {
            getLogger().severe("╔══ Server Error ════════════════════════");
            getLogger().severe("║ LittleNebo requires Paper!");
            getLogger().severe("║ This plugin won't work on Spigot or Bukkit.");
            getLogger().severe("║ Please use Paper: https://papermc.io/");
            getLogger().severe("╚════════════════════════════════════════");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(chatManager, this);

        getCommand("littlenebo").setExecutor(new LittleNeboCommand(this));

        getLogger().info("Little Nebo enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Little Nebo disabled.");
    }

    /**
     * Gets the ConfigManager to access plugin settings
     *
     * @return the active ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Gets the ChatManager that handles processing and rendering chat messages.
     *
     * @return the active ChatManager instance
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * Logs a message at INFO, but only if debug mode is enabled in config.
     *
     * @param message the debug text to log
     */
    public void debug(String message) {
        if (getConfig().getBoolean("debug", false)) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}