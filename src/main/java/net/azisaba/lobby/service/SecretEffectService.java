package net.azisaba.lobby.service;

import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.model.SecretsFile;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SecretEffectService extends BukkitRunnable {
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

    public SecretEffectService(@NotNull AzisabaLobby plugin) {
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
                        int r = (int) (colorData.red * 255);
                        int g = (int) (colorData.green * 255);
                        int b = (int) (colorData.blue * 255);
                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), 1.0f);
                        player.spawnParticle(Particle.DUST, px, py, pz, 0, dustOptions);
                    }
                }
            }
        }
    }

    private record ColorData(double red, double green, double blue) {
    }
}
