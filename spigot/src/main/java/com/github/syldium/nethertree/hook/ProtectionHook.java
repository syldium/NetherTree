package com.github.syldium.nethertree.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionHook {

    boolean canDecay(Player player, Location location);
}
