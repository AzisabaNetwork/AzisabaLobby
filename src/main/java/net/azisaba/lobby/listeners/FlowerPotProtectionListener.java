package net.azisaba.lobby.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class FlowerPotProtectionListener implements Listener {

    @EventHandler
    public void onPot(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if(player.hasPermission("azisabalobby.allow-interact-flower-pot")) return;
        if(block != null && block.getType() == Material.FLOWER_POT) {
            e.setCancelled(true);
        }
    }
}
