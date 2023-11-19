package com.github.syldium.nethertree.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.github.syldium.nethertree.util.BlockHelper.getNearbyBlockByType;

public class NetherTree {

    /** Set of all the tree stems */
    public static final List<Material> STEMS = Arrays.asList(Material.CRIMSON_STEM, Material.WARPED_STEM);

    /** Set of all the tree leaves */
    public static final List<Material> LEAVES = Arrays.asList(Material.NETHER_WART_BLOCK, Material.SHROOMLIGHT, Material.WARPED_WART_BLOCK);

    public static boolean hasStem(Collection<Location> potentialStems, Block leaves, int maxDistance, int maxDistanceSquared) {
        // Search among already known stems
        for (Location knownLog : potentialStems) {
            double distance = NumberConversions.square(knownLog.getX() - leaves.getX()) + NumberConversions.square(knownLog.getY() - leaves.getY()) + NumberConversions.square(knownLog.getZ() - leaves.getZ());
            if (distance <= maxDistanceSquared) {
                return true;
            }
        }

        // Otherwise search among the nearby blocks
        Block block = getNearbyBlockByType(leaves.getLocation(), maxDistance, maxDistanceSquared, STEMS);
        if (block != null) {
            potentialStems.add(block.getLocation());
        }
        return block != null;
    }
}
