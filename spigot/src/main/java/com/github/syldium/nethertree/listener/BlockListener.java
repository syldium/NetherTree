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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockListener implements Listener {

    private final NetherTreePlugin plugin;

    public BlockListener(NetherTreePlugin plugin) {
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (NetherTree.LEAVES.contains(event.getBlock().getType())) {
            event.getBlock().getState().setMetadata("persistent", new FixedMetadataValue(this.plugin, true));
        } else if (NetherTree.LOGS.contains(event.getBlock().getType())) {
            this.plugin.getRunnable(event.getBlock().getWorld()).removeFromScheduledBlocks(NetherTree.getNearbyBlocks(event.getBlock().getLocation(), 5));
        }
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
        for (Block block : NetherTree.getNearbyBlocks(removed.getLocation(), 5)) {
            if (!NetherTree.LEAVES.contains(block.getType())) {
                continue;
            }

            boolean persistent = plugin.getConfig().getBoolean("generation.set-non-persistent-tag");
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
