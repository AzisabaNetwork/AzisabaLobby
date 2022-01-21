package net.azisaba.lobby.gui;

import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.config.ServerInfo;
import net.azisaba.lobby.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerSelectionScreen implements InventoryHolder, Listener {
    public static final Map<String, Integer> PLAYER_COUNT = new HashMap<>();

    public static int getPlayerCount(List<String> servers) {
        int count = 0;
        for (String server : servers) {
            count += Util.clamp(PLAYER_COUNT.getOrDefault(server, 0), 0, Integer.MAX_VALUE);
        }
        return count;
    }

    private final AzisabaLobby plugin;
    private final Inventory inventory = Bukkit.createInventory(this, 54, "サーバー選択");

    public ServerSelectionScreen(AzisabaLobby plugin) {
        this.plugin = plugin;
        update();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void update() {
        this.plugin.getServers().forEach((slot, server) -> {
            int playerCount = getPlayerCount(server.getCountedServers());
            ItemStack item = new ItemStack(server.getMaterial(), Util.clamp(playerCount, 1, 64));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + ChatColor.UNDERLINE + server.getName());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.addAll(server.getDescription().stream().map(s -> ChatColor.WHITE + s).collect(Collectors.toList()));
            lore.add("");
            lore.add(ChatColor.WHITE + "現在" + ChatColor.GREEN + playerCount + ChatColor.WHITE + "人がプレイ中" + Util.repeat("！", (int) Math.floor(playerCount / 30.0)));
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + Util.repeat("-", (int) (server.getMaxDescriptionLength() * 1.5)));
            lore.add("");
            lore.add(ChatColor.WHITE + "状態 : " + ChatColor.DARK_GRAY + server.getStatus());
            if (server.getRecommendedVersion() != null) {
                lore.add(ChatColor.WHITE + "推奨バージョン : " + ChatColor.GOLD + server.getRecommendedVersion());
            }
            if (server.getCompatibleVersion() != null) {
                lore.add(ChatColor.WHITE + "対応バージョン : " + ChatColor.GOLD + server.getCompatibleVersion());
            }
            lore.add("");
            meta.setLore(lore);
            item.setItemMeta(meta);
            this.inventory.setItem(slot, item);
        });
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent e) {
        if (e.getDestination() == null) return;
        if (e.getDestination().getHolder() != this) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory() == null) return;
        if (e.getInventory().getHolder() != this) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() == null || e.getClickedInventory() == null) return;
        if (e.getInventory().getHolder() != this || e.getClickedInventory().getHolder() != this) return;
        ServerInfo server = plugin.getServers().get(e.getSlot());
        if (server == null) return;
        Util.requestConnect(plugin, (Player) e.getWhoClicked(), Util.random(server.getServers()));
    }
}
