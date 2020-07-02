package com.github.syldium.nethertree.runnable;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DecayRunnable extends BukkitRunnable {

    private final NetherTreePlugin plugin;
    private final Random random;
    private final Map<Long, List<Block>> scheduledBlocks;
    private final int randomTickSpeed;

    public DecayRunnable(NetherTreePlugin plugin, Random random, int randomTickSpeed) {
        this(plugin, random, new HashMap<>(), randomTickSpeed);
    }

    public DecayRunnable(NetherTreePlugin plugin, Random random, Map<Long, List<Block>> scheduledBlocks, int randomTickSpeed) {
        this.plugin = plugin;
        this.random = random;
        this.scheduledBlocks = scheduledBlocks;
        this.randomTickSpeed = randomTickSpeed;
    }

    @Override
    public void run() {
        if (this.scheduledBlocks.isEmpty()) {
            this.plugin.unregisterRunnable(this);
            return;
        }
        for (Block block : getSomeScheduledBlocks()) {
            if (!block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
                continue;
            }
            if (NetherTree.LEAVES.contains(block.getType())) {
                LeavesDecayEvent event = new LeavesDecayEvent(block);
                this.plugin.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    block.removeMetadata("persistent", this.plugin);
                    block.breakNaturally();
                }
            }
            this.scheduledBlocks.get(getChunkKey(block.getChunk())).remove(block);
        }
    }

    public boolean addBlock(Block block) {
        long chunkKey = getChunkKey(block.getChunk());
        List<Block> actual = this.scheduledBlocks.computeIfAbsent(chunkKey, s -> new ArrayList<>());
        if (actual.contains(block)) {
            return false;
        }
        return actual.add(block);
    }

     public void removeFromScheduledBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            long chunkKey = getChunkKey(block.getChunk());
            if (scheduledBlocks.containsKey(chunkKey)) {
                scheduledBlocks.get(chunkKey).remove(block);
            }
        }
    }

    private List<Block> getSomeScheduledBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (Iterator<Map.Entry<Long, List<Block>>> it = this.scheduledBlocks.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, List<Block>> entry = it.next();
            if (entry.getValue().isEmpty()) {
                it.remove();
                continue;
            }

            List<Block> scheduled = entry.getValue();
            for (int i = 0; i < this.randomTickSpeed; i++) {
                int n = this.random.nextInt(455);
                if (n < scheduled.size()) {
                    blocks.add(scheduled.get(n));
                }
            }
        }
        return blocks;
    }

    private long getChunkKey(Chunk chunk) {
        return (long)chunk.getX() & 4294967295L | ((long)chunk.getZ() & 4294967295L) << 32;
    }
}
