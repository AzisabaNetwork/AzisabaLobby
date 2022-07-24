package net.azisaba.lobby.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ParticleMenuItem {

  public static ItemStack phItem = new ItemStack(Material.GOLDEN_APPLE);
  static {
    ItemMeta meta = phItem.getItemMeta();
    meta.setDisplayName(ChatColor.AQUA + "パーティクルメニューを開く");
    List<String> lores = new ArrayList<>();
    lores.add(ChatColor.GRAY + "右クリックでパーティクルをつけることができるメニューを開きます!\n");
    meta.setLore(lores);
    phItem.setItemMeta(meta);
  }

}
