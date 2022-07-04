package com.github.syldium.nethertree.util;

import org.bukkit.Chunk;
import org.bukkit.Location;

public final class ChunkKey {

    private ChunkKey() {}

    public static long getChunkKey(Chunk chunk) {
        return getChunkKey(chunk.getX(), chunk.getZ());
    }

    public static long getChunkKey(Location location) {
        return getChunkKey((int) location.getX() >> 4, (int) location.getZ() >> 4);
    }

    static long getChunkKey(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }
}
