package com.github.syldium.nethertree.runnable;

import com.github.syldium.nethertree.NetherTreePlugin;
import org.bukkit.Location;

import java.util.Collections;
import java.util.Random;

/**
 * A dummy variant of DecayRunnable if randomTickSpeed is set to 0.
 */
class DummyDecayRunnable extends DecayRunnable {

    DummyDecayRunnable(NetherTreePlugin plugin) {
        super(plugin, new Random(), Collections.emptyMap(), 0);
    }

    @Override
    public boolean addLocation(Location loc) {
        return true;
    }
}
