package com.github.syldium.nethertree.listener;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class BlockPlaceListener implements Listener {

    private final NetherTreePlugin plugin;

    public BlockPlaceListener(NetherTreePlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (NetherTree.LEAVES.contains(event.getBlock().getType())) {
            event.getBlock().getState().setMetadata("persistent", new FixedMetadataValue(this.plugin, true));
        } else if (NetherTree.LOGS.contains(event.getBlock().getType())) {
            int maxDistance = this.plugin.getConfig().getInt("max-distance-from-log");
            for (Block block : NetherTree.getNearbyBlocks(event.getBlock().getLocation(), maxDistance, NetherTree.LEAVES)) {
                boolean removed = this.plugin.getRunnable(event.getBlock().getWorld()).removeFromScheduledBlocks(block);
                if (!removed) { // If the block was already here, it must be persistent
                    block.getState().setMetadata("persistent", new FixedMetadataValue(this.plugin, true));
                }
            }
        }
    }
}
