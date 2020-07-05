package com.github.syldium.nethertree.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class BlockHelper {

    public static List<Block> getNearbyBlocksByType(Location location, int radius, EnumSet<Material> types) {
        return getNearbyBlocksByType(location, radius, radius * radius, types);
    }

    public static List<Block> getNearbyBlocksByType(Location center, int radius, int radiusSquared, EnumSet<Material> types) {
        World world = center.getWorld();
        Objects.requireNonNull(world, "World cannot be null");

        int bx = center.getBlockX();
        int by = center.getBlockY();
        int bz = center.getBlockZ();

        List<Block> blocks = new ArrayList<>();
        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    double distance = (bx-x) * (bx-x) + (bz-z) * (bz-z) + (by-y) * (by-y);
                    if (distance <= radiusSquared) {
                        Block block = world.getBlockAt(x, y, z);
                        if (types.contains(block.getType())) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static Block getNearbyBlockByType(Location center, int radius, EnumSet<Material> types) {
        return getNearbyBlockByType(center, radius, radius * radius, types);
    }

    public static Block getNearbyBlockByType(Location center, int radius, int radiusSquared, EnumSet<Material> types) {
        World world = center.getWorld();
        Objects.requireNonNull(world, "World cannot be null");

        int bx = center.getBlockX();
        int by = center.getBlockY();
        int bz = center.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    double distance = (bx-x) * (bx-x) + (bz-z) * (bz-z) + (by-y) * (by-y);
                    if (distance <= radiusSquared) {
                        Block block = world.getBlockAt(x, y, z);
                        if (types.contains(block.getType())) {
                            return block;
                        }
                    }
                }
            }
        }
        return null;
    }
}
