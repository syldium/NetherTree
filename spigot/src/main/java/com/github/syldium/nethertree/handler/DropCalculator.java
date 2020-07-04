package com.github.syldium.nethertree.handler;

import com.github.syldium.nethertree.util.NetherTree;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DropCalculator {

    private static final EnumSet<Material> HOES = EnumSet.of(
            Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE, Material.GOLDEN_HOE
    );

    private final Random random = new Random();
    private final boolean alwaysDropWhenHoeUsed;
    private final boolean tntUsePlayersRate;
    private final Map<Material, Integer> baseDrops = new HashMap<>(NetherTree.LEAVES.size());
    private final Map<Material, Integer> playerDrops = new HashMap<>(NetherTree.LEAVES.size());

    public DropCalculator(ConfigurationSection section) {
        this.alwaysDropWhenHoeUsed = section == null || section.getBoolean("always-drop-when-hoe-used", true);
        this.tntUsePlayersRate = section == null || section.getBoolean("tnt-use-players-rate", true);
        for (Material leaves : NetherTree.LEAVES) {
            this.baseDrops.put(leaves, getDropRateFromSection(section, "base." + leaves.name()));
            this.playerDrops.put(leaves, getDropRateFromSection(section, "player." + leaves.name()));
        }
    }

    public boolean shouldDrop(Material material, Entity cause) {
        if (cause instanceof Player && this.alwaysDropWhenHoeUsed && HOES.contains(((Player) cause).getInventory().getItemInMainHand().getType())) {
            return true;
        }

        Map<Material, Integer> table = this.baseDrops;
        if (cause instanceof Player || (this.tntUsePlayersRate && cause instanceof TNTPrimed)) {
            table = this.playerDrops;
        }
        return this.random.nextInt(100) + 1 <= table.get(material);
    }

    public boolean shouldDrop(Block block, Entity cause) {
        return this.shouldDrop(block.getType(), cause);
    }

    private int getDropRateFromSection(ConfigurationSection section, String path) {
        return section == null ? 100 : section.getInt(path, 100);
    }
}
