package com.github.syldium.nethertree.util;

import com.github.syldium.nethertree.NetherTreePlugin;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RunnableHelper {

    public static void loadFromSection(NetherTreePlugin plugin, ConfigurationSection section) {
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            try {
                UUID uuid = UUID.fromString(entry.getKey());
                World world = plugin.getServer().getWorld(uuid);
                List<String> serialized = (List<String>) entry.getValue();
                if (world == null) {
                    throw new IllegalArgumentException("World with uuid " + entry.getKey() + " doesn't exist.");
                }

                plugin.getRunnable(world).deserialize(plugin, serialized, world);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Invalid config: " + e.getMessage());
            }
        }
    }
}
