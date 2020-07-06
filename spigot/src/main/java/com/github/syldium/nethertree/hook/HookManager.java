package com.github.syldium.nethertree.hook;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class HookManager implements ProtectionHook {

    private final List<ProtectionHook> hooks = new ArrayList<>();

    public void registerHook(Plugin plugin) {
        if (plugin != null) {
            this.hooks.add(this.getHookFromPlugin(plugin));
        }
    }

    @Override
    public boolean canDecay(Player player, Location location) {
        for (ProtectionHook hook : this.hooks) {
            if (!hook.canDecay(player, location)) {
                return false;
            }
        }
        return true;
    }

    private ProtectionHook getHookFromPlugin(Plugin plugin) {
        if (plugin instanceof WorldGuardPlugin) {
            return new WorldGuardHook((WorldGuardPlugin) plugin);
        }
        throw new IllegalArgumentException("Unknown hook for " + plugin.getName() + " plugin.");
    }
}
