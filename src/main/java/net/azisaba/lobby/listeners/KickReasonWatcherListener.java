package net.azisaba.lobby.listeners;

import net.azisaba.lobby.util.WebhookUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.Collections;
import java.util.List;

public class KickReasonWatcherListener implements Listener {
    private static final List<String> REASONS = Collections.singletonList("Flying is not enabled on this server");

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        for (String reason : REASONS) {
            if (ChatColor.stripColor(e.getReason()).contains(reason)) {
                String content = "`" + e.getPlayer().getName() + "` (`" + e.getPlayer().getUniqueId() + "`)が`" + e.getReason() + "`でキックされました。(stripColor: `" + ChatColor.stripColor(e.getReason()) + "`)";
                WebhookUtil.sendDiscordWebhook("kick-reason-watcher-webhook-url", null, content);
                return;
            }
        }
    }
}
