package net.azisaba.lobby;

import net.azisaba.lobby.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AzisabaLobby extends JavaPlugin {
  private final Map<Integer, ServerInfo> servers = new HashMap<>();
  private ServerSelectionScreen serverSelectionScreen;

  @Override
  public void onEnable() {
    getConfig().options().copyDefaults();
    reload();
    Bukkit.getPluginManager().registerEvents(new VoidToSpawnListener(this), this);
    Bukkit.getPluginManager().registerEvents(new LobbyItemsListener(this), this);
    Bukkit.getPluginManager().registerEvents(new CheckJoinListener(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerJoinGuideListener(), this);
    Bukkit.getPluginManager().registerEvents(new FlowerPotProtectionListener(), this);
    Bukkit.getPluginManager().registerEvents(new HangingProtectionListener(), this);
    Bukkit.getPluginManager().registerEvents(new PortalListener(this), this);
    Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordPluginMessageListener(this));
    this.serverSelectionScreen = new ServerSelectionScreen(null, this, this.getServers());
    new ServerChecker(this);
    Bukkit.getLogger().info(getName() + " enabled.");
  }

  @Override
  public void onDisable() {
    Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
    Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
    Bukkit.getLogger().info(getName() + " disabled.");
  }

  public ServerSelectionScreen getServerSelectionScreen() {
    return serverSelectionScreen;
  }

  @SuppressWarnings("unchecked")
  public void reload() {
    // TODO: this code sucks
    /*
     * servers:
     *   '0':
     *     material: DIRT
     *     itemDamage: 0
     *     itemTag: "{CustomTags:'aaaa'}"
     *     name: "&aCool server"
     *     servers:
     *     - coolserver1
     *     - coolserver2
     *     countedServers:
     *     - coolserver1
     *     - coolserver2
     *     - coolserver100
     *     - coolserver101
     *     description:
     *     - ":eyes:"
     *     status: "&5公開中"
     *     recommendedVersion: "2.0"
     *     compatibleVersion: "1.RV-pre1 - 2.0"
     *     selectableServers: true # right click to open server menu (a screen which contains coolserver1, and coolserver2)
     */
    servers.clear();
    getConfig().getConfigurationSection("servers").getValues(false).forEach((key, value) -> {
      int slot = Integer.parseInt(key);
      if (slot < 0 || slot >= 54) {
        throw new RuntimeException("Slot is out of range (acceptable range: 0-53, but got " + slot + ")");
      }
      Map<String, Object> map;
      if (value instanceof Map<?, ?>) {
        map = (Map<String, Object>) value;
      } else if (value instanceof ConfigurationSection) {
        map = ((ConfigurationSection) value).getValues(true);
      } else {
        return;
      }
      Material material = Material.getMaterial(map.getOrDefault("material", "STONE").toString());
      if (material == null) material = Material.STONE;
      short itemDamage = ((Number) map.getOrDefault("itemDamage", 0)).shortValue();
      String itemTag = (String) map.get("itemTag");
      String name = ChatColor.translateAlternateColorCodes('&', map.get("name").toString());
      List<String> servers = (List<String>) map.get("servers");
      if (servers == null) {
        servers = Collections.singletonList((String) map.get("server"));
      }
      List<String> countedServers = (List<String>) map.get("countedServers");
      if (countedServers == null) {
        countedServers = servers;
      }
      List<String> description = (List<String>) map.get("description");
      if (description == null) description = Collections.emptyList();
      String status = ChatColor.translateAlternateColorCodes('&', (String) map.getOrDefault("status", "開発中"));
      String recommendedVersion = (String) map.get("recommendedVersion");
      String compatibleVersion = (String) map.get("compatibleVersion");
      if (recommendedVersion != null) {
        recommendedVersion = ChatColor.translateAlternateColorCodes('&', recommendedVersion);
      }
      if (compatibleVersion != null) {
        compatibleVersion = ChatColor.translateAlternateColorCodes('&', compatibleVersion);
      }
      boolean selectableServers = Boolean.parseBoolean(String.valueOf(map.get("selectableServers")));
      boolean selectOnly = Boolean.parseBoolean(String.valueOf(map.get("selectOnly")));
      this.servers.put(
              slot,
              new ServerInfo(
                      material,
                      itemDamage,
                      itemTag,
                      name,
                      servers,
                      countedServers,
                      description.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()),
                      status,
                      recommendedVersion,
                      compatibleVersion,
                      selectableServers,
                      selectOnly
              )
      );
    });
    if (serverSelectionScreen != null) serverSelectionScreen.initItems();
  }

  public Map<Integer, ServerInfo> getServers() {
    return servers;
  }
}
