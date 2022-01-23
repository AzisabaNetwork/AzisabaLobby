package net.azisaba.lobby.config;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

@Data
public class ServerInfo {
    /**
     * Defines the material to show in the inventory. Defaults to STONE.
     */
    @NonNull
    private final Material material;

    /**
     * Defines the item damage value (DV) or durability. Defaults to 0.
     */
    private final short itemDamage;

    /**
     * Defines the item tag. May be null.
     */
    //@Nullable
    private final String itemTag;

    /**
     * Defines the server name. Not null.
     */
    @NonNull
    private final String name;

    /**
     * Defines the servers list which player can join to. The list may be empty.
     */
    @NonNull
    private final List<String> servers;

    /**
     * Defines the servers list which will be counted towards the total player count. The list may be empty.
     */
    @NonNull
    private final List<String> countedServers;

    /**
     * Defines the description of the server. The list may be empty.
     */
    @NonNull
    private final List<String> description;

    /**
     * Defines the status of the server.
     */
    @NonNull
    private final String status;

    /**
     * Defines the recommended version(s) of the server.
     */
    //@Nullable
    private final String recommendedVersion;

    /**
     * Defines the compatible version(s) of the server.
     */
    //@Nullable
    private final String compatibleVersion;

    /**
     * If enabled, the player can right-click to open "servers" menu (which contains entries from <code>servers</code>
     * option)
     */
    private final boolean selectableServers;

    /**
     * If enabled, both left and right will activate the individual "servers" selector. If enabled, this option will
     * override the <code>selectableServers</code> option.
     */
    private final boolean selectOnly;

    public int getMaxDescriptionLength() {
        int length = 12;
        for (String s : description) {
            int i = ChatColor.stripColor(s).length();
            if (length < i) length = i;
        }
        return length;
    }
}
