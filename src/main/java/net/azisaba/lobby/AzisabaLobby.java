package net.azisaba.lobby;

import net.azisaba.azisabaachievements.api.AzisabaAchievementsProvider;
import net.azisaba.azisabaachievements.api.Key;
import net.azisaba.lobby.commands.AzisabaLobbyCommand;
import net.azisaba.lobby.commands.SecretsCommand;
import net.azisaba.lobby.model.ConnectionMethod;
import net.azisaba.lobby.model.Game;
import net.azisaba.lobby.model.SecretsFile;
import net.azisaba.lobby.listener.*;
import net.azisaba.lobby.service.PlayerCountService;
import net.azisaba.lobby.service.SecretEffectService;
import net.azisaba.lobby.util.Util;
import net.azisaba.lobby.util.VoidExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class AzisabaLobby extends JavaPlugin {
    public static @NotNull AzisabaLobby instance() {
        return JavaPlugin.getPlugin(AzisabaLobby.class);
    }

    /**
     * The games shown in the menu. The whole map is replaced on reload so that asynchronous readers
     * (such as {@link PlayerCountService}) never observe a partially populated map.
     */
    private volatile Map<Integer, Game> servers = Collections.emptyMap();

    /**
     * Returns the currently loaded games. The returned map is immutable and is replaced on reload,
     * so it should not be cached across reloads.
     */
    public @NotNull Map<Integer, Game> gameMap() {
        return servers;
    }

    public @Nullable Game gameBySlot(int slot) {
        return servers.get(slot);
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        reload();
        SecretsFile.load(this);
        Bukkit.getPluginManager().registerEvents(new VoidToSpawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GameMenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new CheckJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new FlowerPotProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new HangingProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinGuideListener(), this);
        Bukkit.getPluginManager().registerEvents(new ParticleMenuListener(this), this);
        Bukkit.getPluginManager().registerEvents(new KickReasonWatcherListener(), this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordPluginMessageListener());

        Objects.requireNonNull(Bukkit.getPluginCommand("azisabalobby")).setExecutor(new AzisabaLobbyCommand(this));

        new PlayerCountService().runTaskTimerAsynchronously(this, 20, 200);

        if (Util.isClassPresent("net.azisaba.azisabaachievements.api.AzisabaAchievementsProvider")) {
            VoidExecutor.safeExecute(() -> new VoidExecutor() {
                @Override
                public void execute() {
                    Bukkit.getPluginManager().registerEvents(new JoinAchievementListener(), AzisabaLobby.this);
                    Bukkit.getPluginManager().registerEvents(new SecretInteractListener(AzisabaLobby.this), AzisabaLobby.this);
                    Objects.requireNonNull(Bukkit.getPluginCommand("secrets")).setExecutor(new SecretsCommand(AzisabaLobby.this));
                    AzisabaAchievementsProvider.get()
                            .getAchievementManager()
                            .createAchievement(Key.key("azisaba", "general/lobby/join"), 1, 10)
                            .whenComplete((data, throwable) -> {
                                if (throwable != null) {
                                    // already exists
                                    getLogger().info("Failed to create achievement data: " + throwable.getMessage());
                                } else {
                                    getLogger().info("Created achievement " + data.getKey());
                                }
                            });
                    new SecretEffectService(AzisabaLobby.this);
                }
            });
        }
        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        SecretsFile.save(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    /**
     * Reloads the plugin configuration from disk and rebuilds the game menu.
     * <p>
     * The new games are swapped in only after every entry has been parsed, so a broken configuration never
     * leaves the plugin with a partially loaded menu. If loading fails, the exception is propagated to the
     * caller and the previously loaded games stay in use.
     */
    public void reload() {
        validateConfigFile();
        reloadConfig();
        loadGames();
    }

    /**
     * Parses config.yml before handing it to {@link #reloadConfig()}.
     * <p>
     * {@code reloadConfig()} silently replaces the in-memory configuration with an empty one when the file is
     * not valid YAML, which would empty the game menu. Parsing the file beforehand turns that into an error so
     * that the previous configuration stays in use.
     */
    private void validateConfigFile() {
        final File file = new File(getDataFolder(), "config.yml");
        if (!file.isFile()) {
            return;
        }
        try {
            new YamlConfiguration().load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("config.yml is not valid YAML", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadGames() {
        // TODO: this code sucks
        /*
         * servers:
         *   '0':
         *     material: DIRT
         *     itemDamage: 0
         *     itemTag: "{CustomTags:'aaaa'}"
         *     name: "&aCool server"
         *     servers:
         *     - coolserver1
         *     - coolserver2
         *     countedServers:
         *     - coolserver1
         *     - coolserver2
         *     - coolserver100
         *     - coolserver101
         *     description:
         *     - ":eyes:"
         *     status: "&5公開中"
         *     recommendedVersion: "2.0"
         *     compatibleVersion: "1.RV-pre1 - 2.0"
         *     selectableServers: true # right click to open server menu (a screen which contains coolserver1, and coolserver2)
         *     connectionMethod: proxy # proxy (default) or transfer
         *     transferAddress: "coolserver.example.com:25565" # required when connectionMethod is transfer
         */
        final Map<Integer, Game> loadedGames = new HashMap<>();
        final ConfigurationSection serversSection = getConfig().getConfigurationSection("servers");
        if (serversSection == null) {
            getLogger().warning("No 'servers' section found in config.yml; the game menu will be empty.");
        } else {
            serversSection.getValues(false).forEach((key, value) -> {
                int slot = Integer.parseInt(key);
                if (slot < 0 || slot > 53) {
                    throw new RuntimeException("Slot is out of range (acceptable range: 0-53, but got " + slot + ")");
                }
                Map<String, Object> map;
                if (value instanceof Map<?, ?>) {
                    map = (Map<String, Object>) value;
                } else if (value instanceof ConfigurationSection) {
                    map = ((ConfigurationSection) value).getValues(true);
                } else {
                    return;
                }
                Material material = Material.getMaterial(map.getOrDefault("material", "STONE").toString());
                if (material == null) material = Material.STONE;
                short itemDamage = ((Number) map.getOrDefault("itemDamage", 0)).shortValue();
                String itemTag = (String) map.get("itemTag");
                String name = ChatColor.translateAlternateColorCodes('&', map.get("name").toString());
                List<String> servers = (List<String>) map.get("servers");
                if (servers == null) {
                    servers = map.get("server") == null ? Collections.emptyList() : Collections.singletonList((String) map.get("server"));
                }
                servers = servers.stream().filter(Objects::nonNull).collect(Collectors.toList());
                List<String> countedServers = (List<String>) map.get("countedServers");
                if (countedServers == null) {
                    countedServers = servers;
                }
                countedServers = countedServers.stream().filter(Objects::nonNull).collect(Collectors.toList());
                List<String> description = (List<String>) map.get("description");
                if (description == null) description = Collections.emptyList();
                String status = ChatColor.translateAlternateColorCodes('&', (String) map.getOrDefault("status", "開発中"));
                String recommendedVersion = (String) map.get("recommendedVersion");
                String compatibleVersion = (String) map.get("compatibleVersion");
                if (recommendedVersion != null) {
                    recommendedVersion = ChatColor.translateAlternateColorCodes('&', recommendedVersion);
                }
                if (compatibleVersion != null) {
                    compatibleVersion = ChatColor.translateAlternateColorCodes('&', compatibleVersion);
                }
                boolean selectableServers = Boolean.parseBoolean(String.valueOf(map.get("selectableServers")));
                boolean selectOnly = Boolean.parseBoolean(String.valueOf(map.get("selectOnly")));

                final String rawConnectionMethod = (String) map.get("connectionMethod");
                ConnectionMethod connectionMethod = ConnectionMethod.fromConfig(rawConnectionMethod);
                if (rawConnectionMethod != null && ConnectionMethod.parse(rawConnectionMethod) == null) {
                    getLogger().warning(
                            "Game '" + name + "' has an unknown connectionMethod '" + rawConnectionMethod
                                    + "'. Falling back to proxy."
                    );
                }
                final String rawTransferAddress = (String) map.get("transferAddress");
                InetSocketAddress transferAddress = Util.parseHostAndPort(rawTransferAddress);
                if (connectionMethod == ConnectionMethod.TRANSFER) {
                    if (transferAddress == null) {
                        getLogger().warning(
                                "Game '" + name + "' uses connectionMethod 'transfer' but transferAddress '"
                                        + rawTransferAddress + "' is not a valid host:port; players cannot connect."
                        );
                    }
                    if (selectableServers || selectOnly) {
                        getLogger().warning(
                                "Game '" + name + "' uses connectionMethod 'transfer', so selectableServers / selectOnly"
                                        + " are ignored."
                        );
                        selectableServers = false;
                        selectOnly = false;
                    }
                } else {
                    if (rawTransferAddress != null) {
                        getLogger().warning(
                                "Game '" + name + "' sets transferAddress but connectionMethod is not 'transfer';"
                                        + " transferAddress is ignored."
                        );
                    }
                    transferAddress = null;
                }

                loadedGames.put(
                        slot,
                        new Game(
                                material,
                                itemDamage,
                                itemTag,
                                name,
                                servers,
                                countedServers,
                                description.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()),
                                status,
                                recommendedVersion,
                                compatibleVersion,
                                selectableServers,
                                selectOnly,
                                connectionMethod,
                                transferAddress
                        )
                );
            });
        }

        // Swap in the fully parsed games at once so that a broken configuration never leaves a partial menu.
        this.servers = Collections.unmodifiableMap(loadedGames);
    }

    public @NotNull Object getSecretAchievementKey() {
        return Key.key("azisaba-lobby", getConfig().getString("secret-achievement-key", "secret"));
    }
}
