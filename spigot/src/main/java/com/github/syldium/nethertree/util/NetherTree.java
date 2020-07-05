package com.github.syldium.nethertree.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import java.util.Collection;
import java.util.EnumSet;

import static com.github.syldium.nethertree.util.BlockHelper.getNearbyBlockByType;

public class NetherTree {

    public static final EnumSet<Material> LOGS = EnumSet.of(Material.CRIMSON_STEM, Material.WARPED_STEM);
    public static final EnumSet<Material> LEAVES = EnumSet.of(Material.NETHER_WART_BLOCK, Material.SHROOMLIGHT, Material.WARPED_WART_BLOCK);

    public static boolean hasLog(Collection<Location> potentialLogs, Block leaves, int maxDistance, int maxDistanceSquared) {
        // Search among already known logs
        for (Location knownLog : potentialLogs) {
            double distance = NumberConversions.square(knownLog.getX() - leaves.getX()) + NumberConversions.square(knownLog.getY() - leaves.getY()) + NumberConversions.square(knownLog.getZ() - leaves.getZ());
            if (distance <= maxDistanceSquared) {
                return true;
            }
        }

        // Otherwise search among the nearby blocks
        Block block = getNearbyBlockByType(leaves.getLocation(), maxDistance, maxDistanceSquared, LOGS);
        if (block != null) {
            potentialLogs.add(block.getLocation());
        }
        return block != null;
    }
}
