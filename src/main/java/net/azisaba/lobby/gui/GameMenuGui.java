package net.azisaba.lobby.gui;

import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.model.Game;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class GameMenuGui implements InventoryHolder, Listener {
    private final @NotNull Player player;
    private final @NotNull Inventory inventory = Bukkit.createInventory(this, 54, "ゲームメニュー");

    private final @NotNull BukkitRunnable updateService = new BukkitRunnable() {
        @Override
        public void run() {
            inventory.setItem(4, azisabaItem());
            AzisabaLobby.instance().gameMap().forEach((slot, game) -> inventory.setItem(slot, gameItem(game)));
        }
    };

    public GameMenuGui(final @NotNull Player player) {
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, AzisabaLobby.instance());

        inventory.setItem(38, youTubeItem());
        inventory.setItem(39, twitterItem());
        inventory.setItem(40, discordItem());
        inventory.setItem(41, gitHubItem());
        inventory.setItem(42, wikiItem());

        inventory.setItem(49, closeItem());

        updateService.runTaskTimer(AzisabaLobby.instance(), 0, 15);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getInventory().getHolder() != this) {
            return;
        }

        event.setCancelled(true);

        if (event.getSlot() == 38) {
            sendUrlMessage("https://youtube.com/@AzisabaNetwork");
            return;
        } else if (event.getSlot() == 39) {
            sendUrlMessage("https://twitter.com/@AzisabaNetwork");
            return;
        } else if (event.getSlot() == 40) {
            sendUrlMessage("https://discord.gg/azisaba");
            return;
        } else if (event.getSlot() == 41) {
            sendUrlMessage("https://github.com/AzisabaNetwork");
            return;
        } else if (event.getSlot() == 42) {
            sendUrlMessage("https://wiki.azisaba.net");
            return;
        } else if (event.getSlot() == 49) {
            event.getWhoClicked().closeInventory();
            return;
        }

        final @Nullable Game game = AzisabaLobby.instance().gameBySlot(event.getSlot());
        if (game == null || game.getServers().isEmpty()) return;

        if (shouldOpenGameMenu(game, event.getClick())) {
            final GameGui gameGui = new GameGui(player, game);
            player.openInventory(gameGui.getInventory());
        } else if (event.getClick().isLeftClick()) {
            game.tryConnection(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(final @NotNull InventoryDragEvent event) {
        if (event.getInventory().getHolder() == this) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(final @NotNull InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == this) {
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryDragEvent.getHandlerList().unregister(this);
            updateService.cancel();
            player.updateInventory();
        }
    }

    private void sendUrlMessage(final @NotNull String url) {
        final TextComponent component = new TextComponent(url);
        component.setColor(net.md_5.bungee.api.ChatColor.BLUE);
        component.setUnderlined(true);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        player.sendMessage("");
        player.sendMessage(component);
        player.sendMessage("");
        player.closeInventory();
    }

    private boolean shouldOpenGameMenu(final @NotNull Game game, final @NotNull ClickType clickType) {
        return game.isSelectOnly()
                || (game.isSelectableServers() && clickType.isRightClick())
                || (player.hasPermission("azisabalobby.show-all-server-menu") && clickType == ClickType.MIDDLE);
    }

    private @NotNull ItemStack azisabaItem() {
        final ItemStack itemStack = Skulls.azisaba();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "アジ鯖");
        itemMeta.setLore(Arrays.asList(
                "",
                ChatColor.GREEN + player.getName() + ChatColor.GRAY + "さんアジ鯖にようこそ！",
                ChatColor.DARK_GRAY + "合計" + ChatColor.AQUA + AzisabaLobby.instance().gameMap().values().stream().mapToInt(Game::getPlayerCount).sum() + ChatColor.DARK_GRAY + "人がプレイ中"
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack gameItem(final @NotNull Game game) {
        final ItemStack itemStack = game.toItemStack();

        final ItemMeta itemMeta = itemStack.getItemMeta();
        final List<String> lore = itemMeta.getLore();
        lore.add("");
        if (game.isSelectOnly()) {
            lore.add(ChatColor.GREEN + "クリックして接続サーバーを選択！");
        } else if (game.isSelectableServers()) {
            lore.add(ChatColor.GREEN + "クリックしてプレイ！");
            lore.add(ChatColor.GRAY + "(右クリックして接続サーバーを選択できます)");
        } else {
            lore.add(ChatColor.GREEN + "クリックしてプレイ！");
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack youTubeItem() {
        final ItemStack itemStack = Skulls.youTube();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "YouTube");
        itemMeta.setLore(Arrays.asList(
                ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "https://youtube.com/@AzisabaNetwork",
                ChatColor.GRAY + "(クリックしてURLをチャットで表示します)"
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack twitterItem() {
        final ItemStack itemStack = Skulls.twitter();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "Twitter");
        itemMeta.setLore(Arrays.asList(
                ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "https://twitter.com/@AzisabaNetwork",
                ChatColor.GRAY + "(クリックしてURLをチャットで表示します)"
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack discordItem() {
        final ItemStack itemStack = Skulls.discord();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.BLUE + "Discord");
        itemMeta.setLore(Arrays.asList(
                ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "https://discord.gg/azisaba",
                ChatColor.GRAY + "(クリックしてURLをチャットで表示します)"
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack gitHubItem() {
        final ItemStack itemStack = Skulls.gitHub();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "GitHub");
        itemMeta.setLore(Arrays.asList(
                ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "https://github.com/AzisabaNetwork",
                ChatColor.GRAY + "(クリックしてURLをチャットで表示します)"
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack wikiItem() {
        final ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "Wiki");
        itemMeta.setLore(Arrays.asList(
                ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "https://wiki.azisaba.net",
                ChatColor.GRAY + "(クリックしてURLをチャットで表示します)"
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private @NotNull ItemStack closeItem() {
        final ItemStack itemStack = new ItemStack(Material.ARROW);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "メニューを閉じる");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
