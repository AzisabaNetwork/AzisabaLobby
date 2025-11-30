package net.azisaba.lobby.gui;

import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.model.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameGui implements InventoryHolder, Listener {
    private static final int[] serverSlots = { 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42 };

    private final @NotNull Player player;
    private final @NotNull Inventory inventory;

    private final @NotNull Game game;

    private final @NotNull Map<Integer, String> serverMap = new HashMap<>();
    private final @NotNull BukkitRunnable updateService = new BukkitRunnable() {
        @Override
        public void run() {
            inventory.setItem(4, game.toItemStack());

            int currentServerIndex = 0;
            for (final String server : game.getServers()) {
                final int slot = serverSlots[currentServerIndex++];
                inventory.setItem(slot, serverItem(server));
                serverMap.put(slot, server);
            }
        }
    };

    public GameGui(final @NotNull Player player, final @NotNull Game game) {
        this.player = player;
        this.game = game;
        this.inventory = Bukkit.createInventory(this, 54, String.format("ゲームメニュー > %s", game.getName()));

        Bukkit.getPluginManager().registerEvents(this, AzisabaLobby.instance());

        inventory.setItem(48, previousItem());
        inventory.setItem(49, closeItem());

        updateService.runTaskTimer(AzisabaLobby.instance(), 0, 15);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getInventory().getHolder() != this) {
            return;
        }

        event.setCancelled(true);

        if (event.getSlot() == 4) {
            game.tryConnection(player);
            return;
        }

        if (event.getSlot() == 48) {
            final InventoryHolder gameMenuGui = new GameMenuGui(player);
            player.openInventory(gameMenuGui.getInventory());
            return;
        }

        if (event.getSlot() == 50) {
            player.closeInventory();
            return;
        }

        final @Nullable String server = serverMap.get(event.getSlot());
        if (server != null) {
            game.tryConnection(player, server);
        }
    }

    @EventHandler
    public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(final @NotNull InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == this) {
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryDragEvent.getHandlerList().unregister(this);
            updateService.cancel();
        }
    }

    private @NotNull ItemStack serverItem(final @NotNull String server) {
        final ItemStack itemStack = new ItemStack(game.getMaterial());
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + server);
        final List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN.toString() + game.getPlayerCountOf(server) + ChatColor.GRAY + "人がこのサーバーに接続しています");
        lore.add(ChatColor.GRAY + "(クリックしてこのサーバーに接続します)");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack previousItem() {
        final ItemStack itemStack = new ItemStack(Material.BOOKSHELF);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "ゲームメニューに戻る");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack closeItem() {
        final ItemStack itemStack = new ItemStack(Material.ARROW);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "メニューを閉じる");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
