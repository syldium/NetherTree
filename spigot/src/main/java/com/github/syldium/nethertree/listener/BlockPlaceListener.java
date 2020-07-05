package com.github.syldium.nethertree.listener;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
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
            this.plugin.getTreeHandler().handleStemPlace(event.getBlock());
        }
    }
}
