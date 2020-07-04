package com.github.syldium.nethertree.listener;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockRemoveListener implements Listener {

    private final NetherTreePlugin plugin;

    public BlockRemoveListener(NetherTreePlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLogBreak(BlockBreakEvent event) {
        if (NetherTree.LOGS.contains(event.getBlock().getType())) {
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> handleBlockRemove(event.getBlock(), new ArrayList<>()), 2L);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLogExplode(BlockExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLogExplodeByEntity(EntityExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    private void handleExplosion(List<Block> removed) {
        List<Location> potentialLogs = new ArrayList<>();
        for (Block block : removed) {
            if (NetherTree.LOGS.contains(block.getType())) {
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> handleBlockRemove(block, potentialLogs), 2L);
            }
        }
    }

    private void handleBlockRemove(Block removed, List<Location> potentialLogs) {
        int maxDistance = this.plugin.getConfig().getInt("max-distance-from-log");
        for (Block block : NetherTree.getNearbyBlocks(removed.getLocation(), maxDistance)) {
            if (!NetherTree.LEAVES.contains(block.getType())) {
                continue;
            }

            boolean persistent = false;
            for (MetadataValue metadataValue : block.getState().getMetadata("persistent")) {
                persistent = metadataValue.asBoolean();
            }
            if (persistent) {
                continue;
            }

            if (!NetherTree.hasLog(potentialLogs, block)) {
                this.plugin.getRunnable(block.getWorld()).addBlock(block);
            }
        }
    }
}
