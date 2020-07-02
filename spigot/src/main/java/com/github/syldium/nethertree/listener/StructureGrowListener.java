package com.github.syldium.nethertree.listener;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class StructureGrowListener implements Listener {

    private final NetherTreePlugin plugin;

    public StructureGrowListener(NetherTreePlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.plugin = plugin;
    }

    @EventHandler
    public void onTreeGrow(StructureGrowEvent event) {
        if (!event.getSpecies().equals(TreeType.CRIMSON_FUNGUS) && !event.getSpecies().equals(TreeType.WARPED_FUNGUS)) {
            return;
        }
        if (!plugin.getConfig().getBoolean("generation.set-non-persistent-tag")) {
            return;
        }
        for (BlockState blockState : event.getBlocks()) {
            if (NetherTree.LEAVES.contains(blockState.getType())) {
                blockState.setMetadata("persistent", new FixedMetadataValue(this.plugin, false));
            }
        }
    }
}
