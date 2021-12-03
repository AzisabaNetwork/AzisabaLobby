package net.azisaba.lobby.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HangingProtectionListener implements Listener {

    @EventHandler
    public void onHanging(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (damager instanceof Player) {
            if (damager.hasPermission("azisabalobby.allow-destroy-painting")) return;
        }
        Entity entity = e.getEntity();
        if (entity instanceof Painting || entity instanceof ItemFrame) e.setCancelled(true);
    }

}
