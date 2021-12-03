package net.azisaba.lobby;

import net.azisaba.lobby.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AzisabaLobby extends JavaPlugin {
  @Override
  public void onEnable() {
    getConfig().options().copyDefaults();
    Bukkit.getPluginManager().registerEvents(new VoidToSpawnListener(this), this);
    Bukkit.getPluginManager().registerEvents(new CheckJoinListener(), this);
    Bukkit.getPluginManager().registerEvents(new FlowerPotProtectionListener(), this);
    Bukkit.getPluginManager().registerEvents(new HangingProtectionListener(), this);
    Bukkit.getLogger().info(getName() + " enabled.");
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().info(getName() + " disabled.");
  }
}
