package com.github.syldium.nethertree.commands;

import com.github.syldium.nethertree.NetherTreePlugin;
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
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Nether Tree Configuration has been reloaded!");
        return true;
    }
}
