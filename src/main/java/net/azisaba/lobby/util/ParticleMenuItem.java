package net.azisaba.lobby.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ParticleMenuItem {

  public static ItemStack phItem = new ItemStack(Material.NETHER_STAR);
  static {
    ItemMeta meta = phItem.getItemMeta();
    meta.setDisplayName(ChatColor.AQUA + "パーティクルメニューを開く");
    phItem.setItemMeta(meta);
  }

}
