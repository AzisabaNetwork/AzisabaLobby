package net.azisaba.lobby.listeners;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.azisaba.azisabaachievements.api.AzisabaAchievementsProvider;
import net.azisaba.azisabaachievements.api.Key;
import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.config.SecretsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Set;

@RequiredArgsConstructor
public class SecretInteractListener implements Listener {
    private final AzisabaLobby plugin;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            checkSecret(e.getPlayer(), e.getClickedBlock());
        }
    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent e) {
        if (e.getAnimationType() != PlayerAnimationType.ARM_SWING) return;
        Block block = e.getPlayer().getTargetBlock(null, 5);
        if (block.getType() == Material.AIR || block.getType().name().endsWith("_AIR")) {
            checkSecret(e.getPlayer(), block);
        }
    }

    private void checkSecret(@NonNull Player player, @NonNull Block block) {
        if (!SecretsFile.isSecretLocation(block.getLocation())) {
            return;
        }
        Set<String> locations = SecretsFile.getFoundSecrets(player.getUniqueId());
        int old = locations.size();
        locations.add(SecretsFile.locationToString(block.getLocation()));
        int max = SecretsFile.getSecretLocations().size();
        int curr = locations.size();
        if (curr == old) {
            return; // do nothing
        }
        long count = 1;
        if (curr >= max) {
            // unlock achievement immediately
            count = max;
        }
        // send packet
        AzisabaAchievementsProvider.get()
                .getAchievementManager()
                .progressAchievement(player.getUniqueId(), (Key) plugin.getSecretAchievementKey(), count)
                .whenComplete((result, throwable) -> plugin.getLogger().warning("Failed to progress achievement: " + throwable.getMessage()));

        // play firework effect
        Firework spawnedFirework = player.getWorld().spawn(block.getLocation().clone().add(0.5, 0.5, 0.5), Firework.class, firework -> {
            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffect(FireworkEffect.builder()
                    .withColor(Color.GREEN, Color.YELLOW, Color.RED)
                    .withFade(Color.LIME)
                    .with(FireworkEffect.Type.BALL)
                    .build());
            meta.setPower(0);
            firework.setFireworkMeta(meta);
        });

        // detonate after 100ms (equivalent to 2 ticks or 0.1 seconds)
        Bukkit.getScheduler().runTaskLater(plugin, spawnedFirework::detonate, 2);

        player.sendMessage(ChatColor.GREEN + "あと" + ChatColor.RED + (max - curr) + ChatColor.GREEN + "個！");
    }
}
