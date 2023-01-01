package net.azisaba.lobby.config;

import net.azisaba.lobby.util.MapUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SecretsFile {
    private static final String FILENAME = "secrets.yml";
    private static final Set<String> locations = new HashSet<>();
    private static final Map<UUID, Set<String>> players = new HashMap<>();
    private static BukkitTask saveTask;

    public static void save(@NotNull Plugin plugin) {
        Map<String, Object> map = new HashMap<>();
        map.put("locations", new ArrayList<>(locations));
        map.put("players", MapUtil.map(players, UUID::toString, ArrayList::new));
        ConfigFile.save(plugin, FILENAME, map);
    }

    @SuppressWarnings("unchecked")
    public static void load(@NotNull Plugin plugin) {
        locations.clear();
        players.clear();
        YamlConfiguration config = ConfigFile.loadConfig(plugin, FILENAME);
        locations.addAll(config.getStringList("locations"));
        ConfigurationSection section = config.getConfigurationSection("players");
        Map<String, Object> players = (section == null ? new HashMap<>() : section.getValues(true));
        for (String uuid : players.keySet()) {
            try {
                SecretsFile.players.put(UUID.fromString(uuid), new HashSet<>((Collection<String>) players.get(uuid)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (saveTask == null) {
            // save votes every minute
            saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> save(plugin), 20 * 60, 20 * 60);
        }
    }

    public static void addSecretLocation(@NotNull Location location) {
        locations.add(locationToString(location));
    }

    public static void removeSecretLocation(@NotNull Location location) {
        locations.remove(locationToString(location));
    }

    public static boolean isSecretLocation(@NotNull Location location) {
        return locations.contains(locationToString(location));
    }

    public static @NotNull Set<Location> getSecretLocations() {
        Set<Location> locations = new HashSet<>();
        for (String location : SecretsFile.locations) {
            locations.add(stringToLocation(location));
        }
        return locations;
    }

    public static void clearSecretLocations() {
        locations.clear();
    }

    public static @NotNull Set<String> getFoundSecrets(@NotNull UUID uuid) {
        return players.computeIfAbsent(uuid, k -> new HashSet<>());
    }

    @Contract(pure = true)
    public static @NotNull String locationToString(@NotNull Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull Location stringToLocation(@NotNull String string) {
        String[] split = string.split(",");
        return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }
}
