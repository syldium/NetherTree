package com.github.syldium.nethertree.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * An integration of a block protection plugin.
 */
public interface ProtectionHook {

    /**
     * Determines whether a block can decay at the given location.
     *
     * @param player Original player
     * @param location Block location
     * @return If the hook allows the block to decay
     */
    boolean canDecay(Player player, Location location);
}
