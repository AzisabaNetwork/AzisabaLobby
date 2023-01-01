package net.azisaba.lobby.tasks;

import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.config.SecretsFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SecretEffectTask extends BukkitRunnable {
    private static final int DRAW_DISTANCE = 25;
    private static final List<ColorData> COLORS =
            Arrays.asList(
                    new ColorData(1, 0, 0),
                    new ColorData(1, 0.5, 0),
                    new ColorData(1, 1, 0),
                    new ColorData(0, 1, 0),
                    new ColorData(0, 1, 1),
                    new ColorData(0, 0, 1),
                    new ColorData(1, 0, 1)
            );

    public SecretEffectTask(@NotNull AzisabaLobby plugin) {
        this.runTaskTimer(plugin, 4, 4);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<String> secrets = SecretsFile.getFoundSecrets(player.getUniqueId());
            for (Location location : SecretsFile.getSecretLocations()) {
                if (!Objects.equals(location.getWorld(), player.getWorld())) {
                    continue;
                }
                if (secrets.contains(SecretsFile.locationToString(location))) {
                    continue;
                }
                if (player.getLocation().distance(location) > DRAW_DISTANCE) {
                    continue;
                }
                // draw particle
                for (int x = 0; x <= 5; x++) {
                    for (int z = 0; z <= 5; z++) {
                        double modX = 0.25 + x / 10.0;
                        double modZ = 0.25 + z / 10.0;
                        double px = location.getX() + modX;
                        double py = location.getY() + 0.5;
                        double pz = location.getZ() + modZ;
                        ColorData colorData = COLORS.get((x * z) % COLORS.size());
                        player.spawnParticle(Particle.REDSTONE, px, py, pz, 0, colorData.red, colorData.green, colorData.blue, 1);
                    }
                }
            }
        }
    }

    private static class ColorData {
        public final double red;
        public final double green;
        public final double blue;

        public ColorData(double red, double green, double blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
