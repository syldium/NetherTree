package com.github.syldium.nethertree.runnable;

import com.github.syldium.nethertree.NetherTreePlugin;
import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class DecayRunnable extends BukkitRunnable {

    private final NetherTreePlugin plugin;
    private final Random random;
    private final Map<Long, List<Location>> scheduledBlocks;
    private final int randomTickSpeed;

    public DecayRunnable(NetherTreePlugin plugin, Random random, int randomTickSpeed) {
        this(plugin, random, new HashMap<>(), randomTickSpeed);
    }

    public DecayRunnable(NetherTreePlugin plugin, Random random, Map<Long, List<Location>> scheduledBlocks, int randomTickSpeed) {
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

        for (Location loc : getSomeScheduledLocations()) {
            if (!Objects.requireNonNull(loc.getWorld()).isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                continue;
            }

            Block block = loc.getBlock();
            if (NetherTree.LEAVES.contains(block.getType())) {
                LeavesDecayEvent event = new LeavesDecayEvent(block);
                this.plugin.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    block.removeMetadata("persistent", this.plugin);
                    block.breakNaturally();
                }
            }
            this.scheduledBlocks.get(getChunkKey(block.getLocation())).remove(loc);
        }
    }

    public boolean addBlock(Block block) {
        return this.addLocation(block.getLocation());
    }

    public boolean addLocation(Location loc) {
        long chunkKey = getChunkKey(loc);
        List<Location> actual = this.scheduledBlocks.computeIfAbsent(chunkKey, s -> new ArrayList<>());
        if (actual.contains(loc)) {
            return false;
        }
        return actual.add(loc);
    }

    public void addBlocks(Collection<Block> blocks) {
        for (Block block : blocks) {
            this.addBlock(block);
        }
    }

    public void addLocations(Collection<Location> locations) {
        for (Location loc : locations) {
            this.addLocation(loc);
        }
    }

     public boolean removeFromScheduledBlocks(Block block) {
        long chunkKey = getChunkKey(block.getLocation());
        if (this.scheduledBlocks.containsKey(chunkKey)) {
            return this.scheduledBlocks.get(chunkKey).remove(block.getLocation());
        }
        return false;
    }

    private List<Location> getSomeScheduledLocations() {
        List<Location> blocks = new ArrayList<>();
        for (Iterator<Map.Entry<Long, List<Location>>> it = this.scheduledBlocks.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, List<Location>> entry = it.next();
            if (entry.getValue().isEmpty()) {
                it.remove();
                continue;
            }

            List<Location> scheduled = entry.getValue();
            for (int i = 0; i < this.randomTickSpeed; i++) {
                int n = this.random.nextInt(455);
                if (n < scheduled.size()) {
                    blocks.add(scheduled.get(n));
                }
            }
        }
        return blocks;
    }

    private long getChunkKey(Location location) {
        return (long) location.getBlockX() & 0xffffffffL | ((long) location.getBlockZ() & 0xffffffffL) << 32;
    }

    public List<String> serialize() {
        List<String> locations = new ArrayList<>();
        for (List<Location> scheduled : this.scheduledBlocks.values()) {
            for (Location loc : scheduled) {
                locations.add(String.format("%d:%d:%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            }
        }
        return locations;
    }

    public void deserialize(NetherTreePlugin plugin, List<String> sections, World world) {
        for (String serialized : sections) {
            String[] split = serialized.split(":");
            try {
                Location location = new Location(world, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                this.addLocation(location);
            } catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
                plugin.getLogger().severe("Invalid config: " + e.getMessage());
            }
        }
    }
}
