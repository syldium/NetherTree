package com.github.syldium.nethertree.commands;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.runnable.RunnablesManager;
import com.github.syldium.nethertree.util.NetherTreePermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final NetherTreePlugin plugin;

    public ReloadCommand(NetherTreePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission(NetherTreePermissions.NETHER_TREE_ADMIN)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        plugin.reloadConfig();
        if (plugin.getRunnablesManager() != null) {
            plugin.getRunnablesManager().saveCancelAndClear();
            plugin.setRunnablesManager(new RunnablesManager(plugin));
            plugin.getRunnablesManager().load();
        }
        sender.sendMessage(ChatColor.GREEN + "Nether Tree Configuration has been reloaded!");
        return true;
    }
}
