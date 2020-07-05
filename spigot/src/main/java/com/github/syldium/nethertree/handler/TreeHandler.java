package com.github.syldium.nethertree.handler;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.BlockHelper;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.github.syldium.nethertree.util.ChunkKey.getChunkKey;

public class TreeHandler {

    private final NetherTreePlugin plugin;

    private final Map<Long, Set<Location>> cache = new HashMap<>();
    private final int maxDistance;
    private final int maxDistanceSquared;

    public TreeHandler(NetherTreePlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.plugin = plugin;
        this.maxDistance = plugin.getConfig().getInt("max-distance-from-stem", 4);
        this.maxDistanceSquared = this.maxDistance * (this.maxDistance + 1); // For diagonals
    }

    public void removeStem(Block removed) {
        getPotentialStems(removed.getLocation()).removeIf(loc -> loc.getBlockX() == removed.getX() && loc.getBlockY() == removed.getY() && loc.getBlockZ() == removed.getZ());
    }

    public void handleStemRemove(Block removed) {
        Set<Location> potentialStems = getPotentialStems(removed.getLocation());
        for (Block block : this.getLeavesNearTo(removed)) {
            if (isPersistent(block)) {
                continue;
            }

            if (!NetherTree.hasLog(potentialStems, block, this.maxDistance, this.maxDistanceSquared)) {
                this.plugin.getRunnable(block.getWorld()).addBlock(block);
            }
        }

        this.clearCacheIfNeeded();
    }

    public void handleStemPlace(Block placed) {
        Set<Location> potentialStems = getPotentialStems(placed.getLocation());
        potentialStems.add(placed.getLocation());
        for (Block block : this.getLeavesNearTo(placed)) {
            if (isPersistent(block)) {
                continue;
            }

            boolean removed = this.plugin.getRunnable(placed.getWorld()).removeFromScheduledBlocks(block);
            if (!removed && !NetherTree.hasLog(potentialStems, block, this.maxDistance, this.maxDistanceSquared)) {
                block.getState().setMetadata("persistent", new FixedMetadataValue(this.plugin, true)); // If the block was already here, it must be persistent
            }
        }

        this.clearCacheIfNeeded();
    }

    public List<Block> getLeavesNearTo(Block block) {
        return BlockHelper.getNearbyBlocksByType(block.getLocation(), this.maxDistance, this.maxDistanceSquared, NetherTree.LEAVES);
    }

    public void invalidate(long chunkKey) {
        this.cache.remove(chunkKey);
    }

    private Set<Location> getPotentialStems(Location location) {
        return this.cache.computeIfAbsent(getChunkKey(location), s -> new HashSet<>());
    }

    private boolean isPersistent(Block block) {
        boolean persistent = false;
        for (MetadataValue metadataValue : block.getMetadata("persistent")) {
            persistent = metadataValue.asBoolean();
        }
        return persistent;
    }

    private void clearCacheIfNeeded() {
        if (this.cache.size() > 255) {
            this.cache.clear();
        }
    }
}
