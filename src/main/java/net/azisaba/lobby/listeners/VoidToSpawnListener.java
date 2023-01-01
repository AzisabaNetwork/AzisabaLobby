package net.azisaba.lobby.listeners;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.AzisabaLobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class VoidToSpawnListener implements Listener {

  private final AzisabaLobby plugin;

  @EventHandler
  public void onMoveInVoid(PlayerMoveEvent e) {
    Player p = e.getPlayer();

    if (e.getPlayer().getLocation().getY() >= 0) {
      return;
    }
    if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
      return;
    }

    Location loc = new Location(e.getPlayer().getWorld(), 0.5, 40, 0.5, 0, 0);
    Bukkit.getScheduler().runTaskLater(plugin, () -> p.teleport(loc), 0L);
  }
}
