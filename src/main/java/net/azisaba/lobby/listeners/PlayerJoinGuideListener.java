package net.azisaba.lobby.listeners;

import net.azisaba.lobby.AzisabaLobby;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
                p.sendMessage("");
                TextComponent text = new TextComponent();
                text.addExtra("  ");
                TextComponent separator = new TextComponent(" - ");
                separator.setColor(ChatColor.WHITE.asBungee());
                TextComponent twitter = new TextComponent("Twitter");
                twitter.setColor(ChatColor.AQUA.asBungee());
                twitter.setBold(true);
                twitter.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/AzisabaNetwork"));
                TextComponent discord = new TextComponent("Discord");
                discord.setColor(ChatColor.BLUE.asBungee());
                discord.setBold(true);
                discord.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/azisaba"));
                TextComponent youtube = new TextComponent("YouTube");
                youtube.setColor(ChatColor.RED.asBungee());
                youtube.setBold(true);
                youtube.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/c/AzisabaNetwork"));
                text.addExtra(twitter);
                text.addExtra(separator);
                text.addExtra(discord);
                text.addExtra(separator);
                text.addExtra(youtube);
                p.spigot().sendMessage(text);
                p.sendMessage("");
            }
        }.runTaskLater(JavaPlugin.getPlugin(AzisabaLobby.class), 20*2);
    }
}
