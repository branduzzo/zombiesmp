package me.branduzzo.zombieSMP.commands;

import me.branduzzo.zombieSMP.ZombieSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZombiesCommand implements CommandExecutor {

    private final ZombieSMP plugin;

    public ZombiesCommand(ZombieSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zombies.staff")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /zombies <add|clear> [player]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /zombies add <player>");
                    return true;
                }
                handleAddCommand(sender, args[1]);
                break;

            case "clear":
                if (args.length < 2) {
                    if (sender instanceof Player) {
                        handleClearCommand(sender, sender.getName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /zombies clear <player>");
                    }
                } else {
                    handleClearCommand(sender, args[1]);
                }
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Usage: /zombies <add|clear> [player]");
                break;
        }

        return true;
    }

    private void handleAddCommand(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sendMessage(sender, plugin.getConfig().getString("messages.player_not_found"));
            return;
        }

        if (plugin.getZombieManager().isZombie(target)) {
            sendMessage(sender, plugin.getConfig().getString("messages.already_zombie").replace("{player}", target.getName()));
            return;
        }

        plugin.getZombieManager().makeZombie(target, true);
        sendMessage(sender, plugin.getConfig().getString("messages.zombie_added").replace("{player}", target.getName()));
    }

    private void handleClearCommand(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sendMessage(sender, plugin.getConfig().getString("messages.player_not_found"));
            return;
        }

        if (!plugin.getZombieManager().isZombie(target)) {
            sendMessage(sender, plugin.getConfig().getString("messages.not_zombie").replace("{player}", target.getName()));
            return;
        }

        plugin.getZombieManager().cureZombie(target);
        sendMessage(sender, plugin.getConfig().getString("messages.zombie_cleared").replace("{player}", target.getName()));
    }

    private void sendMessage(CommandSender sender, String message) {
        String prefix = plugin.getConfig().getString("messages.prefix");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }
}
