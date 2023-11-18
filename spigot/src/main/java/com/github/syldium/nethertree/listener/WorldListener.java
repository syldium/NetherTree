package com.github.syldium.nethertree.listener;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.ChunkKey;
import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Objects;

public class WorldListener implements Listener {

    private final NetherTreePlugin plugin;

    public WorldListener(NetherTreePlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        this.plugin.getTreeHandler().invalidate(ChunkKey.getChunkKey(event.getChunk()));
    }

    public static class Paper implements Listener {

        private final NetherTreePlugin plugin;

        public Paper(NetherTreePlugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler(ignoreCancelled = true)
        public void onGameRuleChange(WorldGameRuleChangeEvent event) {
            if (!event.getGameRule().equals(GameRule.RANDOM_TICK_SPEED)) {
                return;
            }
            this.plugin.getServer().getScheduler().runTask(
                    this.plugin,
                    () -> this.plugin.updateRunnable(event.getWorld())
            );
        }
    }
}
