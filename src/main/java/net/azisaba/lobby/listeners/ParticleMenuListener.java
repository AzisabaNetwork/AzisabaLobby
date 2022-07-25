package net.azisaba.lobby.listeners;

import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.AzisabaLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@RequiredArgsConstructor
public class ParticleMenuListener implements Listener {

  public static ItemStack PARTICLE_MENU_ITEM = new ItemStack(Material.NETHER_STAR);
  private final AzisabaLobby plugin;

  static {
    ItemMeta meta = PARTICLE_MENU_ITEM.getItemMeta();
    meta.setDisplayName(ChatColor.LIGHT_PURPLE + "パーティクルメニューを開く!");
    meta.setLore(
        Collections.singletonList(ChatColor.LIGHT_PURPLE + "右クリックでパーティクルをつけることができるメニューを開きます!"));
    PARTICLE_MENU_ITEM.setItemMeta(meta);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    e.getPlayer().getInventory().setItem(2, PARTICLE_MENU_ITEM);
  }

  @EventHandler
  public void onUse(PlayerInteractEvent e) {
    if(PARTICLE_MENU_ITEM.isSimilar(e.getItem())) {
      e.setUseItemInHand(Result.DENY);
      e.setUseInteractedBlock(Result.DENY);
      e.getPlayer().getInventory().setItem(2, null);
      e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CAT_PURREOW, 2, 2);
      Bukkit.dispatchCommand(e.getPlayer(), "ph");
      Bukkit.getScheduler().runTaskLater(plugin, () -> e.getPlayer().getInventory().setItem(2, PARTICLE_MENU_ITEM), 5);
    }
  }

}
