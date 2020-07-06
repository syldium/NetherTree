package com.github.syldium.nethertree;

import com.github.syldium.nethertree.handler.DropCalculator;
import com.github.syldium.nethertree.handler.TreeHandler;
import com.github.syldium.nethertree.hook.HookManager;
import com.github.syldium.nethertree.listener.BlockPlaceListener;
import com.github.syldium.nethertree.listener.BlockRemoveListener;
import com.github.syldium.nethertree.listener.ChunkUnloadListener;
import com.github.syldium.nethertree.runnable.DecayRunnable;
import com.github.syldium.nethertree.runnable.RunnablesManager;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class NetherTreePlugin extends JavaPlugin {

    private DropCalculator dropCalculator;
    private HookManager hookManager;
    private RunnablesManager runnablesManager;
    private TreeHandler treeHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.loadConfig();
        this.treeHandler = new TreeHandler(this);
        this.getServer().getPluginManager().registerEvents(new ChunkUnloadListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockRemoveListener(this), this);

        this.runnablesManager = new RunnablesManager(this);
        this.runnablesManager.load();

        this.hookManager = new HookManager();
        this.hookManager.registerHook(this.getServer().getPluginManager().getPlugin("WorldGuard"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.runnablesManager.save();
    }

    public DecayRunnable getRunnable(World world) {
        return this.runnablesManager.getRunnable(world);
    }

    public boolean unregisterRunnable(DecayRunnable runnable) {
        return this.runnablesManager.unregisterRunnable(runnable);
    }

    private void loadConfig() {
        this.saveDefaultConfig();
        this.getConfig().addDefault("max-distance-from-log", 5);
        this.dropCalculator = new DropCalculator(this.getConfig().getConfigurationSection("drop"));
    }

    public DropCalculator getDropCalculator() {
        return Objects.requireNonNull(dropCalculator, "drop calculator");
    }

    public HookManager getHookManager() {
        return Objects.requireNonNull(hookManager, "hook manager");
    }

    public TreeHandler getTreeHandler() {
        return treeHandler;
    }
}
