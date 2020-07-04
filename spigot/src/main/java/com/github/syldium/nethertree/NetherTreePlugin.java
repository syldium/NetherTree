package com.github.syldium.nethertree;

import com.github.syldium.nethertree.handler.DropCalculator;
import com.github.syldium.nethertree.handler.TreeHandler;
import com.github.syldium.nethertree.listener.BlockPlaceListener;
import com.github.syldium.nethertree.listener.BlockRemoveListener;
import com.github.syldium.nethertree.listener.ChunkUnloadListener;
import com.github.syldium.nethertree.runnable.DecayRunnable;
import com.github.syldium.nethertree.runnable.DummyDecayRunnable;
import com.github.syldium.nethertree.util.RunnableHelper;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public final class NetherTreePlugin extends JavaPlugin {

    private final Map<UUID, DecayRunnable> runnables = new HashMap<>();
    private DropCalculator dropCalculator;
    private TreeHandler treeHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.loadConfig();
        this.treeHandler = new TreeHandler(this);
        this.getServer().getPluginManager().registerEvents(new ChunkUnloadListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockRemoveListener(this), this);

        ConfigurationSection runnables = this.getConfig().getConfigurationSection("runnables");
        if (runnables != null) {
            RunnableHelper.loadFromSection(this, runnables);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Map.Entry<UUID, DecayRunnable> entry : this.runnables.entrySet()) {
            this.getConfig().set("runnables." + entry.getKey().toString(), entry.getValue().serialize());
        }
        this.saveConfig();
    }

    public DecayRunnable getRunnable(World world) {
        Objects.requireNonNull(world, "world");

        return runnables.computeIfAbsent(world.getUID(), (s) -> {
            int randomTickSpeed = Optional.ofNullable(world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED)).orElse(3);
            if (randomTickSpeed < 1) {
                return new DummyDecayRunnable(this);
            }

            DecayRunnable runnable = new DecayRunnable(this, new Random(), randomTickSpeed);
            runnable.runTaskTimer(this, 3L, 9L);
            return runnable;
        });
    }

    public void unregisterRunnable(DecayRunnable runnable) {
        for (UUID uuid : this.runnables.keySet()) {
            if (this.runnables.get(uuid).equals(runnable)) {
                runnable.cancel();
                this.runnables.remove(uuid);
            }
        }
    }

    private void loadConfig() {
        this.saveDefaultConfig();
        this.getConfig().addDefault("max-distance-from-log", 5);
        this.dropCalculator = new DropCalculator(this.getConfig().getConfigurationSection("drop"));
    }

    public DropCalculator getDropCalculator() {
        return Objects.requireNonNull(dropCalculator, "drop calculator");
    }

    public TreeHandler getTreeHandler() {
        return treeHandler;
    }
}
