package net.azisaba.lobby.model;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Data;
import lombok.NonNull;
import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.util.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
public class Game {
    /**
     * Defines the material to show in the inventory. Defaults to STONE.
     */
    @NonNull
    private final Material material;

    /**
     * Defines the item damage value (DV) or durability. Defaults to 0.
     */
    private final short itemDamage;

    /**
     * Defines the item tag. May be null.
     */
    //@Nullable
    private final String itemTag;

    /**
     * Defines the server name. Not null.
     */
    @NonNull
    private final String name;

    /**
     * Defines the servers list which player can join to. The list may be empty.
     */
    @NonNull
    private final List<String> servers;

    /**
     * Defines the servers list which will be counted towards the total player count. The list may be empty.
     */
    @NonNull
    private final List<String> countedServers;

    /**
     * Defines the description of the server. The list may be empty.
     */
    @NonNull
    private final List<String> description;

    /**
     * Defines the status of the server.
     */
    @NonNull
    private final String status;

    /**
     * Defines the recommended version(s) of the server.
     */
    //@Nullable
    private final String recommendedVersion;

    /**
     * Defines the compatible version(s) of the server.
     */
    //@Nullable
    private final String compatibleVersion;

    /**
     * If enabled, the player can right-click to open "servers" menu (which contains entries from <code>servers</code>
     * option)
     */
    private final boolean selectableServers;

    /**
     * If enabled, both left and right will activate the individual "servers" selector. If enabled, this option will
     * override the <code>selectableServers</code> option.
     */
    private final boolean selectOnly;

    private final @NotNull Map<String, Integer> playerCountMap = new ConcurrentHashMap<>();

    public int getPlayerCount() {
        return playerCountMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getPlayerCountOf(final @NotNull String server) {
        return playerCountMap.getOrDefault(server, 0);
    }

    @ApiStatus.Internal
    public void updatePlayerCount(final @NotNull String server, final int playerCount) {
        if (!servers.contains(server)) {
            return;
        }
        playerCountMap.put(server, playerCount);
    }

    @ApiStatus.Internal
    public void requestPlayerCount() {
        final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.isEmpty()) return;
        final Player player = new ArrayList<>(players).get(new Random().nextInt(players.size()));
        requestPlayerCount(player);
    }

    @ApiStatus.Internal
    public void requestPlayerCount(final @NotNull Player player) {
        for (final String server : countedServers) {
            final ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
            bytes.writeUTF("PlayerCount");
            bytes.writeUTF(server);
            player.sendPluginMessage(AzisabaLobby.instance(), "BungeeCord", bytes.toByteArray());
        }
    }

    public void tryConnection(final @NotNull Player player) {
        final String server = servers.get(new Random().nextInt(servers.size()));
        tryConnection(player, server);
    }

    public void tryConnection(final @NotNull Player player, final @NotNull String server) {;
        final ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
        bytes.writeUTF("Connect");
        bytes.writeUTF(server);
        player.sendPluginMessage(AzisabaLobby.instance(), "BungeeCord", bytes.toByteArray());
    }

    public @NotNull ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material, 1, itemDamage);

        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + name);
        itemMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_POTION_EFFECTS
        );

        final List<String> lore = new ArrayList<>();
        lore.add("");
        lore.addAll(description.stream().map(s -> ChatColor.GRAY + s).collect(Collectors.toList()));
        lore.add("");
        lore.add(ChatColor.WHITE + "状態：" + ChatColor.GRAY + status);
        if (recommendedVersion != null) {
            lore.add(ChatColor.WHITE + "推奨バージョン：" + ChatColor.GREEN + recommendedVersion);
        }
        if (compatibleVersion != null) {
            lore.add(ChatColor.WHITE + "対応バージョン：" + ChatColor.YELLOW + compatibleVersion);
        }
        lore.add("");
        lore.add(ChatColor.GREEN + "クリックしてプレイ！");
        lore.add(ChatColor.GRAY.toString() + getPlayerCount() + "人がプレイ中");
        itemMeta.setLore(lore);

        if (itemTag != null) {
            final Object nbtTag = NMSUtil.parseTag(itemTag);
            itemStack = NMSUtil.setTag(itemStack, nbtTag);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
