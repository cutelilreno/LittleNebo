package moe.reno.littlenebo;

import moe.reno.littlenebo.chat.ChatManager;
import moe.reno.littlenebo.commands.LittleNeboCommand;
import moe.reno.littlenebo.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Little Nebo.
 * <p>
 * Handles enabling/disabling the plugin, instantiating managers, and
 * wiring up event listeners and commands.
 * </p>
 */
public class LittleNebo extends JavaPlugin {
    private ConfigManager configManager;
    private ChatManager chatManager;

    @Override
    public void onEnable() {
        // Initialize config manager
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize chat manager
        chatManager = new ChatManager(this, configManager);

        // Register events
        getServer().getPluginManager().registerEvents(chatManager, this);

        // Register commands
        getCommand("littlenebo").setExecutor(new LittleNeboCommand(this));

        getLogger().info("Little Nebo has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Little Nebo has been disabled!");
    }

    /**
     * Gets the ConfigManager, which provides access to all plugin settings
     * and chat formats.
     *
     * @return the active ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Gets the ChatManager, responsible for formatting and rendering chat messages.
     *
     * @return the active ChatManager instance
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * Logs a message at INFO level, but only if debug mode is enabled in config.
     *
     * @param message the debug text to log
     */
    public void debug(String message) {
        if (getConfig().getBoolean("debug", false)) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}