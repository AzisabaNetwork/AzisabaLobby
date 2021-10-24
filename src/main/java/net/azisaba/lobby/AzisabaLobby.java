package net.azisaba.lobby;

import net.azisaba.lobby.listeners.CheckJoinListener;
import net.azisaba.lobby.listeners.VoidToSpawnListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AzisabaLobby extends JavaPlugin {
  private LuckPerms luckPerms;

  @Override
  public void onEnable() {
    getConfig().options().copyDefaults();
    Bukkit.getPluginManager().registerEvents(new VoidToSpawnListener(this), this);
    Bukkit.getPluginManager().registerEvents(new CheckJoinListener(this), this);
    Bukkit.getPluginCommand("azisabalobby").setExecutor(new AzisabaLobbyCommand(this));

    // late init task
    Bukkit.getScheduler().runTaskLater(this, () -> {
      // Get LuckPerms API
      luckPerms = LuckPermsProvider.get();
    }, 1);
    Bukkit.getLogger().info(getName() + " enabled.");
  }

  public LuckPerms getLuckPerms() {
    return luckPerms;
  }

  public boolean isAllowedToFly(Player player) {
    return getConfig()
            .getStringList("allow-flight-groups")
            .stream()
            .anyMatch(name -> hasGroup(player, name));
  }

  public boolean hasGroup(Player player, String groupName) {
    User user = getLuckPerms().getPlayerAdapter(Player.class).getUser(player);
    String server = getLuckPerms().getServerName();
    return user.getInheritedGroups(QueryOptions.contextual(ImmutableContextSet.of("server", server)))
            .stream()
            .anyMatch(group -> group.getName().equals(groupName));
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().info(getName() + " disabled.");
  }
}
