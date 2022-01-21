package net.azisaba.lobby.config;

import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

@Data
public class ServerInfo {
    private final Material material;
    private final short itemDamage;
    private final String itemTag;
    private final String name;
    private final List<String> servers;
    private final List<String> countedServers;
    private final List<String> description;
    private final String status;
    private final String recommendedVersion;
    private final String compatibleVersion;

    public int getMaxDescriptionLength() {
        int length = 12;
        for (String s : description) {
            int i = ChatColor.stripColor(s).length();
            if (length < i) length = i;
        }
        return length;
    }
}
