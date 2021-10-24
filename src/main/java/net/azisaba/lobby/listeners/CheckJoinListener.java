package net.azisaba.lobby.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CheckJoinListener implements Listener {
    @EventHandler
    public void checkOnJoin(PlayerJoinEvent e) {
        checkGameMode(e.getPlayer());
        e.getPlayer().setAllowFlight(e.getPlayer().hasPermission("azisabalobby.allow-flight"));
    }

    private static void checkGameMode(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("azisabalobby.allow-creative-mode")) {
            player.setGameMode(GameMode.ADVENTURE);
        }
        if (player.getGameMode() == GameMode.SURVIVAL && !player.hasPermission("azisabalobby.allow-survival-mode")) {
            player.setGameMode(GameMode.ADVENTURE);
        }
        if (player.getGameMode() == GameMode.SPECTATOR && !player.hasPermission("azisabalobby.allow-spectator-mode")) {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }
}
