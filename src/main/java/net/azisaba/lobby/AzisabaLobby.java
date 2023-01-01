package net.azisaba.lobby;

import net.azisaba.azisabaachievements.api.AzisabaAchievementsProvider;
import net.azisaba.azisabaachievements.api.Key;
import net.azisaba.lobby.commands.SecretsCommand;
import net.azisaba.lobby.config.SecretsFile;
import net.azisaba.lobby.config.ServerInfo;
import net.azisaba.lobby.gui.ServerSelectionScreen;
import net.azisaba.lobby.listeners.BungeeCordPluginMessageListener;
import net.azisaba.lobby.listeners.CheckJoinListener;
import net.azisaba.lobby.listeners.FlowerPotProtectionListener;
import net.azisaba.lobby.listeners.HangingProtectionListener;
import net.azisaba.lobby.listeners.JoinAchievementListener;
import net.azisaba.lobby.listeners.ParticleMenuListener;
import net.azisaba.lobby.listeners.SecretInteractListener;
import net.azisaba.lobby.listeners.ServerSelectorListener;
import net.azisaba.lobby.listeners.KickReasonWatcherListener;
import net.azisaba.lobby.listeners.PortalListener;
import net.azisaba.lobby.listeners.VoidToSpawnListener;
import net.azisaba.lobby.listeners.PlayerJoinGuideListener;
import net.azisaba.lobby.tasks.SecretEffectTask;
import net.azisaba.lobby.util.ServerChecker;
import net.azisaba.lobby.util.Util;
import net.azisaba.lobby.util.VoidExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AzisabaLobby extends JavaPlugin {
  private final Map<Integer, ServerInfo> servers = new HashMap<>();
  private ServerSelectionScreen serverSelectionScreen;

  @Override
  public void onEnable() {
    getConfig().options().copyDefaults();
    reload();
    Bukkit.getPluginManager().registerEvents(new VoidToSpawnListener(this), this);
    Bukkit.getPluginManager().registerEvents(new ServerSelectorListener(this), this);
    Bukkit.getPluginManager().registerEvents(new CheckJoinListener(), this);
    Bukkit.getPluginManager().registerEvents(new FlowerPotProtectionListener(), this);
    Bukkit.getPluginManager().registerEvents(new HangingProtectionListener(), this);
    Bukkit.getPluginManager().registerEvents(new PortalListener(this), this);
    Bukkit.getPluginManager().registerEvents(new PlayerJoinGuideListener(), this);
    Bukkit.getPluginManager().registerEvents(new ParticleMenuListener(this), this);
    Bukkit.getPluginManager().registerEvents(new KickReasonWatcherListener(), this);
    Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordPluginMessageListener());
    this.serverSelectionScreen = new ServerSelectionScreen(null, this, this.getServers());
    new ServerChecker(this);
    if (Util.isClassPresent("net.azisaba.azisabaachievements.api.AzisabaAchievementsProvider")) {
      VoidExecutor.safeExecute(() -> new VoidExecutor() {
        @Override
        public void execute() {
          Bukkit.getPluginManager().registerEvents(new JoinAchievementListener(), AzisabaLobby.this);
          Bukkit.getPluginManager().registerEvents(new SecretInteractListener(AzisabaLobby.this), AzisabaLobby.this);
          Objects.requireNonNull(Bukkit.getPluginCommand("secrets")).setExecutor(new SecretsCommand(AzisabaLobby.this));
          AzisabaAchievementsProvider.get()
                  .getAchievementManager()
                  .createAchievement(Key.key("azisaba", "general/lobby/join"), 1, 10)
                  .whenComplete((data, throwable) -> {
                    if (throwable != null) {
                      // already exists
                      getLogger().info("Failed to create achievement data: " + throwable.getMessage());
                    } else {
                      getLogger().info("Created achievement " + data.getKey());
                    }
                  });
          new SecretEffectTask(AzisabaLobby.this);
        }
      });
    }
    Bukkit.getLogger().info(getName() + " enabled.");
  }

  @Override
  public void onDisable() {
    SecretsFile.save(this);
    Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
    Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
    Bukkit.getLogger().info(getName() + " disabled.");
  }

  public ServerSelectionScreen getServerSelectionScreen() {
    return serverSelectionScreen;
  }

  public static AzisabaLobby getInstance() {
    return JavaPlugin.getPlugin(AzisabaLobby.class);
  }

  @SuppressWarnings("unchecked")
  public void reload() {
    reloadConfig();

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
      if (slot < 0 || slot > 53) {
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

    SecretsFile.load(this);
  }

  public Map<Integer, ServerInfo> getServers() {
    return servers;
  }

  public @NotNull Object getSecretAchievementKey() {
    return Key.key("azisaba-lobby", getConfig().getString("secret-achievement-key", "secret"));
  }
}
