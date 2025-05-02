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