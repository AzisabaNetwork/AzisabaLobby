package net.azisaba.lobby.listeners;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@RequiredArgsConstructor
public class ServerSelectorListener implements Listener {
    private static final ItemStack SERVER_SELECTOR_ITEM = new ItemStack(Util.findAny("RAW_FISH", "COD"));
    private final AzisabaLobby plugin;

    static {
        ItemMeta meta = SERVER_SELECTOR_ITEM.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "サーバーに遊びに行く！");
            meta.setLore(Arrays.asList(ChatColor.GRAY + "右クリックでサーバー選択画面を開きます！", ChatColor.GRAY + "食用ではありません！"));
            SERVER_SELECTOR_ITEM.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().getInventory().setItem(0, SERVER_SELECTOR_ITEM);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if (SERVER_SELECTOR_ITEM.isSimilar(e.getItem())) {
            e.setUseItemInHand(Event.Result.DENY);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.getPlayer().getInventory().setItem(0, null);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 2);
            e.getPlayer().openInventory(plugin.getServerSelectionScreen().getInventory());
            Bukkit.getScheduler().runTaskLater(plugin, () -> e.getPlayer().getInventory().setItem(0, SERVER_SELECTOR_ITEM), 5);
        }
    }
}
