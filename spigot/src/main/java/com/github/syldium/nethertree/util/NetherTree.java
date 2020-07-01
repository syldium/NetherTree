package com.github.syldium.nethertree.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class NetherTree {

    public static final EnumSet<Material> LOGS = EnumSet.of(Material.CRIMSON_STEM, Material.WARPED_STEM);
    public static final EnumSet<Material> LEAVES = EnumSet.of(Material.NETHER_WART_BLOCK, Material.SHROOMLIGHT, Material.WARPED_WART_BLOCK);

    public static boolean hasLog(List<Location> potentialLogs, Block leaves) {
        // Search among already known logs
        for (Location knownLog : potentialLogs) {
            double distance = NumberConversions.square(knownLog.getX() - leaves.getX()) + NumberConversions.square(knownLog.getY() - leaves.getY()) + NumberConversions.square(knownLog.getZ() - leaves.getZ());
            if (distance < 16) {
                return true;
            }
        }

        // Otherwise search among the nearby blocks
        for (Block block : getNearbyBlocks(leaves.getLocation(), 4)) {
            if (Tag.LOGS.isTagged(block.getType())) {
                potentialLogs.add(block.getLocation());
                return true;
            }
        }
        return false;
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        World world = location.getWorld();
        Objects.requireNonNull(world, "World cannot be null");

        int layer = (radius * 2) + 1;
        List<Block> blocks = new ArrayList<>(layer * layer * layer);
        for (double x = location.getX() - radius; x <= location.getX() + radius; x++) {
            for (double y = location.getY() - radius; y <= location.getY() + radius; y++) {
                for (double z = location.getZ() - radius; z <= location.getZ() + radius; z++) {
                    blocks.add(world.getBlockAt((int) x,(int) y,(int) z));
                }
            }
        }
        return blocks;
    }
}
