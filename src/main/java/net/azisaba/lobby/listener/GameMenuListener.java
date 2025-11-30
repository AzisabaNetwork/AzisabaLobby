package net.azisaba.lobby.listener;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.gui.GameMenuGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;

public class GameMenuListener implements Listener {
    private static final ItemStack SERVER_SELECTOR_ITEM = createGameMenuItem();

    private static @NotNull ItemStack createGameMenuItem() {
        final ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1);
        itemStack.setDurability((short) 3);
        final SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "ゲームメニュー");
        itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + "右クリックでゲームの選択画面を開きます！"));
        final PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID(), null);
        playerProfile.setProperty(new ProfileProperty("textures", "ewogICJ0aW1lc3RhbXAiIDogMTc2NDUxMjgzMTc5OCwKICAicHJvZmlsZUlkIiA6ICI0OThjYTc2ZGYwODM0NzhmOGY0NjdjOGY1OTQwMjk1MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJHdWx0cm8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM1OGI1YWRmOGVmZjgxZDI1NGNmYmI3MTAxMjFjZjM1NTE4M2E1OGVjMjJlZTRlM2UzNDUxMDFlZTcyNTUxOCIKICAgIH0KICB9Cn0="));
        itemMeta.setPlayerProfile(playerProfile);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        event.getPlayer().getInventory().setItem(0, SERVER_SELECTOR_ITEM);
    }

    @EventHandler
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        if (!SERVER_SELECTOR_ITEM.isSimilar(event.getItem())) {
            return;
        }

        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

        final Player player = event.getPlayer();
        player.getInventory().setItem(0, null);
        player.playSound(event.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 2);
        player.openInventory(new GameMenuGui(event.getPlayer()).getInventory());
        Bukkit.getScheduler().runTaskLater(AzisabaLobby.instance(), () -> event.getPlayer().getInventory().setItem(0, SERVER_SELECTOR_ITEM), 5);
    }
}
