package net.azisaba.lobby.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PreventInventoryMoveListener implements Listener {
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getWhoClicked().getGameMode() != GameMode.ADVENTURE) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getGameMode() != GameMode.ADVENTURE) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onOffhand(PlayerSwapHandItemsEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.ADVENTURE) return;
        e.setCancelled(true);
    }
}
