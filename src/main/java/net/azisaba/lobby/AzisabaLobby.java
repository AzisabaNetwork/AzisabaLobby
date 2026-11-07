package net.azisaba.lobby;

import net.azisaba.azisabaachievements.api.AzisabaAchievementsProvider;
import net.azisaba.azisabaachievements.api.Key;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class AzisabaLobby extends JavaPlugin {
    public static @NotNull AzisabaLobby instance() {
        return JavaPlugin.getPlugin(AzisabaLobby.class);
    }

    private final Map<Integer, Game> servers = new HashMap<>();

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

    @SuppressWarnings("unchecked")
    public void reload() {
        reloadConfig();

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
        servers.clear();
        getConfig().getConfigurationSection("servers").getValues(false).forEach((key, value) -> {
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

            this.servers.put(
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

        SecretsFile.load(this);
    }

    public @NotNull Object getSecretAchievementKey() {
        return Key.key("azisaba-lobby", getConfig().getString("secret-achievement-key", "secret"));
    }
}
