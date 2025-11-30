package net.azisaba.lobby.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class FlowerPotProtectionListener implements Listener {
    @EventHandler
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (player.hasPermission("azisabalobby.allow-interact-flower-pot")) return;
        if (block != null && block.getType() == Material.FLOWER_POT) {
            event.setCancelled(true);
        }
    }
}
