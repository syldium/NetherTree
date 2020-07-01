package com.github.syldium.nethertree.runnable;

import org.bukkit.block.Block;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DecayRunnable extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final Random random;
    private final List<Block> scheduledBlocks;

    private final int base;
    private final int group;

    public DecayRunnable(JavaPlugin plugin, Random random, int randomTickSpeed) {
        this(plugin, random, new ArrayList<>(), randomTickSpeed);
    }

    public DecayRunnable(JavaPlugin plugin, Random random, List<Block> scheduledBlocks, int randomTickSpeed) {
        this.plugin = plugin;
        this.random = random;
        this.scheduledBlocks = scheduledBlocks;
        this.base = 43;
        this.group = 6;
    }

    @Override
    public void run() {
        for (Block block : pickSomeScheduledBlocks()) {
            LeavesDecayEvent event = new LeavesDecayEvent(block);
            this.plugin.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                block.removeMetadata("persistent", this.plugin);
                block.breakNaturally();
            }
        }
    }

    public boolean addBlock(Block block) {
        if (this.scheduledBlocks.contains(block)) {
            return false;
        }
        return this.scheduledBlocks.add(block);
    }

    private List<Block> pickSomeScheduledBlocks() {
        if (this.scheduledBlocks.isEmpty()) {
            return Collections.emptyList();
        }

        int n = (this.scheduledBlocks.size() >> group);
        if (this.random.nextInt(50) > base) {
            n += 1;
        }

        List<Block> blocks = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            blocks.add(this.scheduledBlocks.remove(this.random.nextInt(this.scheduledBlocks.size())));
        }
        return blocks;
    }
}
