package net.azisaba.lobby;

import net.azisaba.lobby.config.ServerInfo;
import net.azisaba.lobby.gui.ServerSelectionScreen;
import net.azisaba.lobby.listeners.BungeeCordPluginMessageListener;
import net.azisaba.lobby.listeners.CheckJoinListener;
import net.azisaba.lobby.listeners.FlowerPotProtectionListener;
import net.azisaba.lobby.listeners.HangingProtectionListener;
import net.azisaba.lobby.listeners.LobbyItemsListener;
import net.azisaba.lobby.listeners.VoidToSpawnListener;
import net.azisaba.lobby.util.ServerChecker;
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
    Bukkit.getPluginManager().registerEvents(new FlowerPotProtectionListener(), this);
    Bukkit.getPluginManager().registerEvents(new HangingProtectionListener(), this);
    Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordPluginMessageListener(this));
    serverSelectionScreen = new ServerSelectionScreen(this);
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
    /*
     * servers:
     *   '0':
     *     material: DIRT
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
      String recommendedVersion = ChatColor.translateAlternateColorCodes('&', (String) map.get("recommendedVersion"));
      String compatibleVersion = ChatColor.translateAlternateColorCodes('&', (String) map.get("compatibleVersion"));
      this.servers.put(
              slot,
              new ServerInfo(
                      material,
                      name,
                      servers,
                      countedServers,
                      description.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()),
                      status,
                      recommendedVersion,
                      compatibleVersion
              )
      );
    });
    if (serverSelectionScreen != null) serverSelectionScreen.update();
  }

  public Map<Integer, ServerInfo> getServers() {
    return servers;
  }
}
