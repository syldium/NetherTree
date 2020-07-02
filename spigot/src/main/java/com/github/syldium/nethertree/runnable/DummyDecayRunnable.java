package com.github.syldium.nethertree.runnable;

import com.github.syldium.nethertree.NetherTreePlugin;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Random;

public class DummyDecayRunnable extends DecayRunnable {

    public DummyDecayRunnable(NetherTreePlugin plugin) {
        super(plugin, new Random(), new HashMap<>(0), 0);
    }

    @Override
    public boolean addBlock(Block block) {
        return true;
    }
}
