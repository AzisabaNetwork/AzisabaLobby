package net.azisaba.lobby.listeners;

import net.azisaba.lobby.AzisabaLobby;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinGuideListener implements Listener {

    ChatColor colorAqua = ChatColor.AQUA;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                p.sendMessage(colorAqua + "ようこそアジ鯖へ！");
                p.sendMessage(colorAqua + "アイテムを使用するか、ロビー奥の各NPCをパンチすることで各サーバーへログインすることができます。");
                p.sendMessage(colorAqua + "各サーバーの詳細情報については公式ホームページを参照してくださいね！");
                p.sendMessage(colorAqua + "https://www.azisaba.net/serverlist/");
            }
        }.runTaskLater(JavaPlugin.getPlugin(AzisabaLobby.class), 20*5);
    }
}
