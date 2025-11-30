package net.azisaba.lobby.service;

import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.model.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCountService extends BukkitRunnable {
    @Override
    public void run() {
        for (final Game game : AzisabaLobby.instance().gameMap().values()) {
            game.requestPlayerCount();
        }
    }
}
