package com.github.syldium.nethertree.runnable;

import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Random;

public class DummyDecayRunnable extends DecayRunnable {

    public DummyDecayRunnable(JavaPlugin plugin) {
        super(plugin, new Random(), new ArrayList<>(0), 0);
    }

    @Override
    public boolean addBlock(Block block) {
        return true;
    }
}
