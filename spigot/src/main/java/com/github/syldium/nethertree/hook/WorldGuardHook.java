package com.github.syldium.nethertree.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class WorldGuardHook implements ProtectionHook {

    private final WorldGuardPlugin worldGuard;

    WorldGuardHook(WorldGuardPlugin worldGuard) {
        this.worldGuard = worldGuard;
    }

    @Override
    public boolean canDecay(Player player, Location location) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        LocalPlayer localPlayer = player == null ? null : this.worldGuard.wrapPlayer(player);
        return query.testState(BukkitAdapter.adapt(location), localPlayer, Flags.LEAF_DECAY);
    }
}
