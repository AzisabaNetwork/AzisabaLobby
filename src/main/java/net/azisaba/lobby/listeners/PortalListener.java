package net.azisaba.lobby.listeners;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

@RequiredArgsConstructor
public class PortalListener implements Listener {
    private final AzisabaLobby plugin;

    @EventHandler
    public void onPlayerMove(PlayerPortalEvent e) {
        Util.requestConnect(plugin, e.getPlayer(), "vanilife");
        e.setCancelled(true);
    }
}
