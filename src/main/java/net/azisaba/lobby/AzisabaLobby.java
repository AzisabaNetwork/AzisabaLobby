package net.azisaba.lobby;

import net.azisaba.lobby.listeners.VoidToSpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AzisabaLobby extends JavaPlugin {

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(new VoidToSpawnListener(this), this);

    Bukkit.getLogger().info(getName() + " enabled.");
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().info(getName() + " disabled.");
  }
}
