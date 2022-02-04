package net.azisaba.lobby.gui;

import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.config.ServerInfo;
import net.azisaba.lobby.util.NMSUtil;
import net.azisaba.lobby.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
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

    private final Inventory previousInventory;
    private final AzisabaLobby plugin;
    private final Map<Integer, ServerInfo> servers;
    private final Inventory inventory = Bukkit.createInventory(this, 54, "サーバー選択");

    public ServerSelectionScreen(Inventory previousInventory, AzisabaLobby plugin, Map<Integer, ServerInfo> servers) {
        this.previousInventory = previousInventory;
        this.plugin = plugin;
        this.servers = servers;
        initItems();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void initItems() {
        inventory.clear();
        servers.forEach((slot, server) -> {
            int playerCount = getPlayerCount(server.getCountedServers());
            ItemStack item = new ItemStack(server.getMaterial(), Util.clamp(playerCount, 1, 64), server.getItemDamage());
            if (server.getItemTag() != null) {
                Object tag = NMSUtil.parseTag(server.getItemTag());
                item = NMSUtil.setTag(item, tag);
            }
            plugin.getLogger().warning("Item is null: " + server);
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + ChatColor.UNDERLINE + server.getName());
                meta.addItemFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_DESTROYS,
                        ItemFlag.HIDE_PLACED_ON,
                        ItemFlag.HIDE_UNBREAKABLE,
                        ItemFlag.HIDE_POTION_EFFECTS
                );
                item.setItemMeta(meta);
            }
            this.inventory.setItem(slot, item);
        });
        updateItems();
    }

    public void updateItems() {
        servers.forEach((slot, server) -> {
            int playerCount = getPlayerCount(server.getCountedServers());
            ItemStack item = inventory.getItem(slot);
            if (item == null) return;
            ItemMeta meta = item.getItemMeta();
            item.setAmount(Util.clamp(playerCount, 1, 64));
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
        });
    }

    @Override
    public Inventory getInventory() {
        return inventory;
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
        if (e.getInventory().getHolder() != this) return;
        e.setCancelled(true);
        if (e.getClickedInventory().getHolder() != this) return;
        ServerInfo server = servers.get(e.getSlot());
        if (server == null || server.getServers().isEmpty()) return;
        if (e.getWhoClicked().hasPermission("azisabalobby.show-all-server-menu") && e.getClick() == ClickType.MIDDLE) {
            List<String> list = new ArrayList<>();
            for (String s : server.getServers()) {
                if (!list.contains(s)) list.add(s);
            }
            for (String s : server.getCountedServers()) {
                if (!list.contains(s)) list.add(s);
            }
            Map<Integer, ServerInfo> serverInfoList = new HashMap<>();
            int idx = 0;
            for (String s : list) {
                if (idx >= 54) continue;
                serverInfoList.put(idx++, new ServerInfo(
                        server.getMaterial(),
                        server.getItemDamage(),
                        server.getItemTag(),
                        server.getName() + " > " + s,
                        Collections.singletonList(s),
                        Collections.singletonList(s),
                        server.getDescription(),
                        server.getStatus(),
                        server.getRecommendedVersion(),
                        server.getCompatibleVersion(),
                        false,
                        false
                ));
            }
            e.getWhoClicked().openInventory(new ServerSelectionScreen(inventory, plugin, serverInfoList).inventory);
            return;
        }
        if (!server.isSelectOnly() && (!server.isSelectableServers() || e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT)) {
            Util.requestConnect(plugin, (Player) e.getWhoClicked(), Util.random(server.getServers()));
        } else if (server.isSelectOnly() || e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
            Map<Integer, ServerInfo> serverInfoList = new HashMap<>();
            int idx = 0;
            for (String s : server.getServers()) {
                if (idx >= 54) continue;
                serverInfoList.put(idx, new ServerInfo(
                        server.getMaterial(),
                        server.getItemDamage(),
                        server.getItemTag(),
                        server.getName() + " > " + s,
                        Collections.singletonList(s),
                        Collections.singletonList(s),
                        server.getDescription(),
                        server.getStatus(),
                        server.getRecommendedVersion(),
                        server.getCompatibleVersion(),
                        false,
                        false
                ));
                idx++;
            }
            e.getWhoClicked().openInventory(new ServerSelectionScreen(inventory, plugin, serverInfoList).inventory);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory() != null && e.getInventory().getHolder() == this && previousInventory != null) {
            Bukkit.getScheduler().runTask(plugin, () -> e.getPlayer().openInventory(previousInventory));
        }
    }
}
