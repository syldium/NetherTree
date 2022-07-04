package com.github.syldium.nethertree.runnable;

import com.github.syldium.nethertree.NetherTreePlugin;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Centralize all runnables.
 */
public class RunnablesManager {

    private final NetherTreePlugin plugin;

    private final File fileConfig;
    private final Map<UUID, DecayRunnable> runnables = new HashMap<>();

    public RunnablesManager(NetherTreePlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.plugin = plugin;

        this.fileConfig = new File(plugin.getDataFolder(), "runnables.yml");
        if (!this.fileConfig.exists()) {
            this.fileConfig.getParentFile().mkdirs();
            try {
                this.fileConfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get or create the runnable for this world.
     *
     * @param world World
     * @return Scheduled runnable
     */
    public DecayRunnable getRunnable(World world) {
        Objects.requireNonNull(world, "world");

        return this.runnables.computeIfAbsent(world.getUID(), (s) -> {
            int randomTickSpeed = Optional.ofNullable(world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED)).orElse(3);
            if (randomTickSpeed < 1) {
                return new DummyDecayRunnable(this.plugin);
            }

            DecayRunnable runnable = new DecayRunnable(this.plugin, new Random(), randomTickSpeed);
            runnable.runTaskTimer(this.plugin, 3L, 9L);
            return runnable;
        });
    }

    /**
     * Cancel and remove a runnable.
     *
     * @param runnable Runnable instance
     * @return If it could be cancelled.
     */
    public boolean unregisterRunnable(DecayRunnable runnable) {
        for (UUID uuid : this.runnables.keySet()) {
            if (this.runnables.get(uuid).equals(runnable)) {
                runnable.cancel();
                this.runnables.remove(uuid);
                return true;
            }
        }
        return false;
    }

    public void load() {
        getFileConfiguration(true).ifPresent(config -> this.loadFromSection(config.getValues(false)));
    }

    public void save() {
        getFileConfiguration(false).ifPresent(config -> {
            for (Map.Entry<UUID, DecayRunnable> entry : this.runnables.entrySet()) {
                config.set(entry.getKey().toString(), entry.getValue().serialize());
            }

            try {
                config.save(this.fileConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Optional<FileConfiguration> getFileConfiguration(boolean load) {
        FileConfiguration config = new YamlConfiguration();
        if (!load) {
            return Optional.of(config);
        }

        try {
            config.load(this.fileConfig);
            return Optional.of(config);
        } catch (IOException|InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void loadFromSection(Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            try {
                UUID uuid = UUID.fromString(entry.getKey());
                World world = this.plugin.getServer().getWorld(uuid);
                List<String> serialized = (List<String>) entry.getValue();
                if (world == null) {
                    throw new IllegalArgumentException("World with uuid " + entry.getKey() + " doesn't exist.");
                }

                this.getRunnable(world).deserialize(this.plugin, serialized, world);
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().severe("Invalid config: " + e.getMessage());
            }
        }
    }
}
