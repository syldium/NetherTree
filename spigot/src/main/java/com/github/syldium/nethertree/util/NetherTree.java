package com.github.syldium.nethertree.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class NetherTree {

    public static final EnumSet<Material> LOGS = EnumSet.of(Material.CRIMSON_STEM, Material.WARPED_STEM);
    public static final EnumSet<Material> LEAVES = EnumSet.of(Material.NETHER_WART_BLOCK, Material.SHROOMLIGHT, Material.WARPED_WART_BLOCK);

    public static boolean hasLog(List<Location> potentialLogs, List<Location> nothingHere, Block leaves, int maxDistance, int maxDistanceSquared) {
        // Search among already known logs
        for (Location knownLog : potentialLogs) {
            double distance = NumberConversions.square(knownLog.getX() - leaves.getX()) + NumberConversions.square(knownLog.getY() - leaves.getY()) + NumberConversions.square(knownLog.getZ() - leaves.getZ());
            if (distance <= maxDistanceSquared) {
                return true;
            }
        }

        // Otherwise search among the nearby blocks
        for (Block block : getNearbyBlocks(leaves.getLocation(), maxDistance, nothingHere)) {
            if (LOGS.contains(block.getType())) {
                potentialLogs.add(block.getLocation());
                return true;
            }
            nothingHere.add(block.getLocation());
        }
        return false;
    }

    public static List<Block> getNearbyBlocks(Location location, int radius, Collection<Location> ignore) {
        World world = location.getWorld();
        Objects.requireNonNull(world, "World cannot be null");

        List<Block> blocks = new ArrayList<>();
        for (double x = location.getX() - radius; x <= location.getX() + radius; x++) {
            for (double y = location.getY() - radius; y <= location.getY() + radius; y++) {
                for (double z = location.getZ() - radius; z <= location.getZ() + radius; z++) {
                    if (notContains((int) x, (int) y, (int) z, ignore)) {
                        blocks.add(world.getBlockAt((int) x, (int) y, (int) z));
                    }
                }
            }
        }
        return blocks;
    }

    private static boolean notContains(int x, int y, int z, Collection<Location> ignore) {
        // Custom .contains() - Compare only coordinates
        for (Location location : ignore) {
            if (location.getBlockX() == x && location.getBlockY() == y && location.getBlockZ() == z) {
                return false;
            }
        }
        return true;
    }
}
