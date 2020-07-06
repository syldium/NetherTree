package com.github.syldium.nethertree.listener;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;
import java.util.Objects;

public class BlockRemoveListener implements Listener {

    private final NetherTreePlugin plugin;

    public BlockRemoveListener(NetherTreePlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLogBreak(BlockBreakEvent event) {
        if (NetherTree.LOGS.contains(event.getBlock().getType())) {
            if (this.plugin.getHookManager().canDecay(event.getPlayer(), event.getBlock().getLocation())) {
                this.plugin.getTreeHandler().removeStem(event.getBlock());
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.plugin.getTreeHandler().handleStemRemove(event.getBlock()), 2L);
            }
        } else if (NetherTree.LEAVES.contains(event.getBlock().getType())) {
            event.setDropItems(this.plugin.getDropCalculator().shouldDrop(event.getBlock(), event.getPlayer()));
            event.getBlock().removeMetadata("persistent", this.plugin);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLogExplode(BlockExplodeEvent event) {
        handleExplosion(event.blockList(), null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLogExplodeByEntity(EntityExplodeEvent event) {
        handleExplosion(event.blockList(), event.getEntity());
    }

    private void handleExplosion(List<Block> removed, Entity cause) {
        for (Block block : removed) {
            if (NetherTree.LOGS.contains(block.getType())) {
                if (this.plugin.getHookManager().canDecay(null, block.getLocation())) {
                    this.plugin.getTreeHandler().removeStem(block);
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.plugin.getTreeHandler().handleStemRemove(block), 2L);
                }
            } else if (NetherTree.LEAVES.contains(block.getType())) {
                block.removeMetadata("persistent", this.plugin);
                if (!this.plugin.getDropCalculator().shouldDrop(block, cause)) {
                    block.setType(Material.AIR);
                }
            }
        }
    }
}
