package net.azisaba.lobby.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class CheckJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        checkGameMode(player);
        player.setAllowFlight(player.hasPermission("azisabalobby.allow-flight"));
    }

    private static void checkGameMode(final @NotNull Player player) {
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
