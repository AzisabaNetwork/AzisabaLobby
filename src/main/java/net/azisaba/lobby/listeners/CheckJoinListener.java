package net.azisaba.lobby.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CheckJoinListener implements Listener {
    @EventHandler
    public void checkOnJoin(PlayerJoinEvent e) {
        e.getPlayer().setAllowFlight(e.getPlayer().hasPermission("azisabalobby.allow-flight"));
    }
}
