package net.azisaba.lobby.listeners;

import net.azisaba.azisabaachievements.api.AzisabaAchievementsProvider;
import net.azisaba.azisabaachievements.api.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.TimeUnit;

public class JoinAchievementListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        AzisabaAchievementsProvider.get()
                .getScheduler()
                .builder(() ->
                        AzisabaAchievementsProvider.get()
                                .getAchievementManager()
                                .progressAchievement(e.getPlayer().getUniqueId(), Key.key("azisaba", "general/lobby/join"), 1))
                .sync()
                .delay(2, TimeUnit.SECONDS)
                .schedule();
    }
}
