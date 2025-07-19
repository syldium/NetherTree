package com.github.syldium.nethertree;

import com.github.syldium.nethertree.commands.ReloadCommand;
import com.github.syldium.nethertree.handler.DropCalculator;
import com.github.syldium.nethertree.handler.TreeHandler;
import com.github.syldium.nethertree.hook.HookManager;
import com.github.syldium.nethertree.listener.BlockPlaceListener;
import com.github.syldium.nethertree.listener.BlockRemoveListener;
import com.github.syldium.nethertree.listener.WorldListener;
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
        this.getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockRemoveListener(this), this);
        this.loadCommands();
        try {
            Class.forName("io.papermc.paper.event.world.WorldGameRuleChangeEvent");
            this.getServer().getPluginManager().registerEvents(new WorldListener.Paper(this), this);
        } catch (ClassNotFoundException ignored) {
        }

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

    public void updateRunnable(World world) {
        this.runnablesManager.updateRunnable(world);
    }

    private void loadConfig() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.getConfig().addDefault("max-distance-from-log", 5);
        loadConfigurableClasses();
    }

    private void loadConfigurableClasses(){
        this.dropCalculator = new DropCalculator(this.getConfig().getConfigurationSection("drop"));
        this.runnablesManager = new RunnablesManager(this);
        this.treeHandler = new TreeHandler(this);
    }

    private void loadCommands() {
        Objects.requireNonNull(this.getCommand("ntreload")).setExecutor(new ReloadCommand(this));
    }

    public DropCalculator getDropCalculator() {
        return Objects.requireNonNull(this.dropCalculator, "drop calculator");
    }

    public HookManager getHookManager() {
        return Objects.requireNonNull(this.hookManager, "hook manager");
    }

    public TreeHandler getTreeHandler() {
        return this.treeHandler;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (runnablesManager != null) {
            this.runnablesManager.save();
        }
        loadConfigurableClasses();
        runnablesManager.load();
    }
}
