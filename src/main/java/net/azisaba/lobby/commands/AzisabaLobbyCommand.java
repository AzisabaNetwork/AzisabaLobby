package net.azisaba.lobby.commands;

import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.AzisabaLobby;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class AzisabaLobbyCommand implements TabExecutor {
    public static final String RELOAD_PERMISSION = "azisabalobby.command.reload";

    private final AzisabaLobby plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/" + label + " reload");
            return true;
        }

        if (!args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.RED + "不明なサブコマンドです: " + args[0]);
            sender.sendMessage(ChatColor.RED + "/" + label + " reload");
            return true;
        }

        if (!sender.hasPermission(RELOAD_PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "このコマンドを実行する権限がありません");
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + "/" + label + " reload");
            return true;
        }

        // Commands are executed on the main thread, so the configuration can be reloaded synchronously.
        try {
            plugin.reload();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reload configuration", e);
            sender.sendMessage(ChatColor.RED + "設定の再読み込みに失敗しました。ゲーム一覧は再読み込み前のままです。詳細はコンソールを確認してください。");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "設定を再読み込みしました。(" + plugin.gameMap().size() + "件のゲーム)");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(RELOAD_PERMISSION)) {
            return filter(Stream.of("reload"), args[0]).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static @NotNull Stream<String> filter(@NotNull Stream<String> stream, @NotNull String str) {
        final String lowerCase = str.toLowerCase(Locale.ROOT);
        return stream.filter(s -> s.toLowerCase(Locale.ROOT).startsWith(lowerCase));
    }
}
