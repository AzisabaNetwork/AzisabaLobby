package net.azisaba.lobby.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HangingProtectionListener implements Listener {

    @EventHandler
    public void onHanging(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Painting) e.setCancelled(true);
    }

}
