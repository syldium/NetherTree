package com.github.syldium.nethertree.handler;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.github.syldium.nethertree.util.ChunkKey.getChunkKey;

public class TreeHandler {

    private final NetherTreePlugin plugin;

    private final Map<Long, List<Location>> cache = new HashMap<>();
    private final int maxDistance;
    private final int maxDistanceSquared;

    public TreeHandler(NetherTreePlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.plugin = plugin;
        this.maxDistance = plugin.getConfig().getInt("max-distance-from-log", 4);
        this.maxDistanceSquared = (int) NumberConversions.square(this.maxDistance);
    }

    public void removeLog(Block removed) {
        getPotentialLogs(removed.getLocation()).removeIf(loc -> loc.getBlockX() == removed.getX() && loc.getBlockY() == removed.getY() && loc.getBlockZ() == removed.getZ());
    }

    public void handleLogRemove(Block removed) {
        List<Location> potentialLogs = getPotentialLogs(removed.getLocation());
        for (Block block : NetherTree.getNearbyBlocks(removed.getLocation(), this.maxDistance, NetherTree.LEAVES)) {
            boolean persistent = false;
            for (MetadataValue metadataValue : block.getState().getMetadata("persistent")) {
                persistent = metadataValue.asBoolean();
            }
            if (persistent) {
                continue;
            }

            if (!NetherTree.hasLog(potentialLogs, block, this.maxDistance, this.maxDistanceSquared)) {
                this.plugin.getRunnable(block.getWorld()).addBlock(block);
            }
        }

        this.clearCacheIfNeeded();
    }

    public void invalidate(long chunkKey) {
        this.cache.remove(chunkKey);
    }

    private List<Location> getPotentialLogs(Location location) {
        return this.cache.computeIfAbsent(getChunkKey(location), s -> new ArrayList<>());
    }

    private void clearCacheIfNeeded() {
        if (this.cache.size() > 255) {
            this.cache.clear();
        }
    }
}
