package net.azisaba.lobby.listeners;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.AzisabaLobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class CheckJoinListener implements Listener {
    private final AzisabaLobby plugin;

    @EventHandler
    public void checkOnJoin(PlayerJoinEvent e) {
        boolean canFly = plugin.isAllowedToFly(e.getPlayer());
        e.getPlayer().setAllowFlight(canFly);
    }
}
