/**
 * MIT License
 * Copyright (c) 2025 cutelilreno
 * https://opensource.org/licenses/MIT
 */
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

    public LittleNeboCommand(LittleNebo plugin) {
        this.plugin = plugin;
    }

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

    /**
     * Sends plugin information to the command sender.
     * <p>
     *     This includes the plugin version and usage instructions.
     * </p>
     *
     * @param sender the command sender
     * @param label  the command label (e.g., "littlenebo")
     */
    private void sendPluginInfo(CommandSender sender, String label) {
        sender.sendMessage(Messages.title("Little Nebo v" + plugin.getPluginMeta().getVersion()));
        sender.sendMessage(Messages.info("Usage: /" + label + " <reload | debug>"));
    }

    /**
     * Handles the reload command.
     * Cant believe im documenting private methods now. Thanks deepsource!
     * @param sender the command sender
     */
    private void handleReload(CommandSender sender) {
        try {
            plugin.getConfigManager().loadConfig();
            sender.sendMessage(Messages.success("LittleNebo: Configuration reloaded."));
        } catch (Exception e) {
            sender.sendMessage(Messages.error("LittleNebo: Failed to reload configuration!"));
            e.printStackTrace();
        }
    }

    /**
     * Handles the debug command and its subcommands.
     *
     * @param sender the command sender
     * @param args   the command arguments
     */
    private void handleDebugCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            boolean debug = plugin.getConfigManager().toggleDebug();
            sender.sendMessage(Messages.success("Debug mode " + (debug ? "enabled" : "disabled") + "!"));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "config" -> {
                ConfigManager cm = plugin.getConfigManager();
                sender.sendMessage(Messages.title("Little Nebo Config:"));
                sender.sendMessage(Messages.info(" • debug: " + cm.isDebugEnabled()));
                sender.sendMessage(Messages.info(" • parse-player-colors: " + cm.isPlayerLegacyColorsEnabled()));
            }
            case "test" -> {
                /* Not as helpful, but good for server admins to test formats on live servers
                   probably a good idea to allow admins to pick the perm to test rather than force them to keep changing
                   thru their perm plugins. Pretty unimportant feature so low priority.
                 */
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

    /**
     * Sends an error message to the sender for an unknown subcommand.
     *
     * @param sender the command sender
     * @param label  the command label (e.g., "littlenebo")
     * @param subCommand the unknown subcommand
     */
    private void sendUnknownSubcommand(CommandSender sender, String label, String subCommand) {
        sender.sendMessage(Messages.error("Unknown subcommand: " + subCommand));
        sender.sendMessage(Messages.info("Usage: /" + label + " <reload | debug>"));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return subcommands.stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("debug")) {
                return Arrays.asList("config", "test").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }
    return new ArrayList<>();
    }
}
