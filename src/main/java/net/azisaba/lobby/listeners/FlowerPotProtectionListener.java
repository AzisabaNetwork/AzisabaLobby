package net.azisaba.lobby.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FlowerPotProtectionListener implements Listener {

    @EventHandler
    public void onPot(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if(player.hasPermission("azisabalobby.allow-interact-flower-pot")) return;
        if(item != null) {
            if(item.getType() == Material.FLOWER_POT) {
                e.setCancelled(true);
            }
        }
    }
}
