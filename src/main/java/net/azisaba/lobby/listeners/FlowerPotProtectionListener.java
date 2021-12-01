package net.azisaba.lobby.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class FlowerPotProtectionListener implements Listener {

    @EventHandler
    public void onPot(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(player.hasPermission("azisabalobby.allow-flower-pot")) return;
        e.setCancelled(true);
    }
}
