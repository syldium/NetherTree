package com.github.syldium.nethertree;

import com.github.syldium.nethertree.listener.BlockListener;
import com.github.syldium.nethertree.listener.StructureGrowListener;
import com.github.syldium.nethertree.runnable.DecayRunnable;
import com.github.syldium.nethertree.runnable.DummyDecayRunnable;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public final class NetherTreePlugin extends JavaPlugin {

    private final Map<UUID, DecayRunnable> runnables = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        this.getServer().getPluginManager().registerEvents(new StructureGrowListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DecayRunnable getRunnable(World world) {
        Objects.requireNonNull(world, "world");

        return runnables.computeIfAbsent(world.getUID(), (s) -> {
            int randomTickSpeed = Optional.ofNullable(world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED)).orElse(3);
            if (randomTickSpeed < 1) {
                return new DummyDecayRunnable(this);
            }

            DecayRunnable runnable = new DecayRunnable(this, new Random(), randomTickSpeed);
            runnable.runTaskTimer(this, 3L, 27L / randomTickSpeed);
            return runnable;
        });
    }
}
