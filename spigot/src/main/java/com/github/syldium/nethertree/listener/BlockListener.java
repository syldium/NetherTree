package com.github.syldium.nethertree.listener;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
        if (!NetherTree.LOGS.contains(event.getBlock().getType())) {
            return;
        }

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            List<Location> potentialLogs = new ArrayList<>();
            for (Block block : NetherTree.getNearbyBlocks(event.getBlock().getLocation(), 5)) {
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
        }, 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLogBreak(BlockPlaceEvent event) {
        if (NetherTree.LEAVES.contains(event.getBlock().getType())) {
            event.getBlock().getState().setMetadata("persistent", new FixedMetadataValue(this.plugin, true));
        }
    }
}
