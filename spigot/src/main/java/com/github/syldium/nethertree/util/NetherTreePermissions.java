package com.github.syldium.nethertree.util;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class NetherTreePermissions {
    public static final Permission NETHER_TREE_ADMIN = new Permission("nethertree.admin", "Allows reloading the config for this plugin", PermissionDefault.OP);
}
