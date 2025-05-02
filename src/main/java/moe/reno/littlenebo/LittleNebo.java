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