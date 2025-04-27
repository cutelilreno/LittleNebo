package moe.reno.littlenebo.commands;

import moe.reno.littlenebo.LittleNebo;
import moe.reno.littlenebo.config.ConfigManager;
import moe.reno.littlenebo.util.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the /littlenebo (alias /nebo) command and its subcommands
 * (reload, debug, etc.), and provides tab‐completion for them.
 */
public class LittleNeboCommand implements CommandExecutor, TabCompleter {
    private final LittleNebo plugin;
    private final List<String> subcommands = Arrays.asList("reload", "debug");

    /**
     * Creates a new command handler tied to the main plugin instance.
     * @param plugin the LittleNebo plugin
     */
    public LittleNeboCommand(LittleNebo plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the /littlenebo command.
     * <p>
     * Valid subcommands:
     * <ul>
     *   <li>reload — reloads the plugin config</li>
     *   <li>debug [config|test …] — toggles debug or shows config/preview</li>
     * </ul>
     * </p>
     *
     * @param sender  who ran the command
     * @param command the command object
     * @param label   the alias used
     * @param args    subcommand and its arguments
     * @return always true (we handle our own error messages)
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("littlenebo.admin")) {
            sender.sendMessage(Messages.error("You don't have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            sendPluginInfo(sender, label);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload" -> handleReload(sender);
            case "debug" -> handleDebugCommand(sender, args);
            default -> sendUnknownSubcommand(sender, label, subCommand);
        }
        return true;
    }

    private void sendPluginInfo(CommandSender sender, String label) {
        sender.sendMessage(Messages.title("Little Nebo v" + plugin.getDescription().getVersion()));
        sender.sendMessage(Messages.info("Usage: /" + label + " <reload | debug>"));
    }

    private void handleReload(CommandSender sender) {
        try {
            plugin.reloadConfig();
            sender.sendMessage(Messages.success("Little Nebo configuration reloaded!"));
        } catch (Exception e) {
            sender.sendMessage(Messages.error("Failed to reload Little Nebo configuration!"));
            e.printStackTrace();
        }
    }

    private void handleDebugCommand(CommandSender sender, String[] args) {
        // /littlenebo debug
        if (args.length == 1) {
            boolean debug = plugin.getConfigManager().toggleDebug();
            sender.sendMessage(Messages.success("Debug mode " + (debug ? "enabled" : "disabled") + "!"));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "config" -> {
                // /littlenebo debug config
                ConfigManager cm = plugin.getConfigManager();
                sender.sendMessage(Messages.title("⎡ Little Nebo Config ⎤"));
                sender.sendMessage(Messages.info(" • debug: " + cm.isDebugEnabled()));
                sender.sendMessage(Messages.info(" • parse-player-colors: " + cm.isParsePlayerColors()));
                sender.sendMessage(Messages.info(" • max-format-checks: " + cm.getMaxFormatChecks()));
            }
            case "test" -> {
                // /littlenebo debug test <some text>
                if (args.length < 3) {
                    sender.sendMessage(Messages.error("Usage: /littlenebo debug test <message…>"));
                    return;
                }
                String sample = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                Component formatted = plugin.getChatManager().formatMessage(
                        (sender instanceof Player ? (Player) sender : null),
                        sample
                );
                sender.sendMessage(Messages.title("Preview:"));
                sender.sendMessage(formatted);
            }
            default -> {
                sender.sendMessage(Messages.error("Unknown debug subcommand: " + args[1]));
                sender.sendMessage(Messages.info("Usage: /littlenebo debug [config|test <message>]"));
            }
        }
    }


    private void sendUnknownSubcommand(CommandSender sender, String label, String subCommand) {
        sender.sendMessage(Messages.error("Unknown subcommand: " + subCommand));
        sender.sendMessage(Messages.info("Usage: /" + label + " <reload | debug>"));
    }

    /**
     * Provides tab‐completion for the first argument
     * (one of “reload” or “debug”).
     *
     * @param sender  who is tab‐completing
     * @param command the command object
     * @param alias   the alias used
     * @param args    the current args typed by the user
     * @return a list of matching subcommands, or empty if none
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return subcommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
