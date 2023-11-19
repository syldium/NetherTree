package com.github.syldium.nethertree.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BlockHelper {

    private BlockHelper() {}

    public static List<Block> getNearbyBlocksByType(Block location, int radius, List<Material> types) {
        return getNearbyBlocksByType(location, radius, radius * radius, types);
    }

    public static List<Block> getNearbyBlocksByType(Block center, int radius, int radiusSquared, List<Material> types) {
        World world = center.getWorld();
        Objects.requireNonNull(world, "World cannot be null");

        int bx = center.getX();
        int by = center.getY();
        int bz = center.getZ();

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

    public static @Nullable Block getNearbyBlockByType(Location center, int radius, List<Material> types) {
        return getNearbyBlockByType(center, radius, radius * radius, types);
    }

    public static @Nullable Block getNearbyBlockByType(Location center, int radius, int radiusSquared, List<Material> types) {
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
