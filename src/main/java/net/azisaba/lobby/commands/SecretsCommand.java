package net.azisaba.lobby.commands;

import lombok.RequiredArgsConstructor;
import net.azisaba.azisabaachievements.api.AzisabaAchievementsProvider;
import net.azisaba.azisabaachievements.api.Key;
import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.config.SecretsFile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class SecretsCommand implements TabExecutor {
    private final AzisabaLobby plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/secrets (add|remove|list|clear)");
            return true;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("add")) {
            Block block = player.getTargetBlock(null, 5);
            if (block == null) {
                sender.sendMessage(ChatColor.RED + "追加したいブロックに視点を合わせてください");
                return true;
            }
            SecretsFile.addSecretLocation(block.getLocation());
            sender.sendMessage(block.getLocation() + "を追加ヨシ！");
        } else if (args[0].equalsIgnoreCase("remove")) {
            Block block = player.getTargetBlock(null, 5);
            if (block == null) {
                sender.sendMessage(ChatColor.RED + "追加したいブロックに視点を合わせてください");
                return true;
            }
            SecretsFile.removeSecretLocation(block.getLocation());
            sender.sendMessage(block.getLocation() + "を削除ヨシ！");
        } else if (args[0].equalsIgnoreCase("list")) {
            for (Location secretLocation : SecretsFile.getSecretLocations()) {
                sender.sendMessage(secretLocation.toString());
            }
        } else if (args[0].equalsIgnoreCase("clear")) {
            SecretsFile.clearSecretLocations();
            sender.sendMessage("全消しヨシ！");
        } else if (args[0].equalsIgnoreCase("createAchievement")) {
            AzisabaAchievementsProvider.get()
                    .getAchievementManager()
                    .createAchievement((Key) plugin.getSecretAchievementKey(), SecretsFile.getSecretLocations().size(), 0) // 0 point because it is seasonal
                    .whenComplete((data, throwable) -> {
                        if (throwable != null) {
                            sender.sendMessage(ChatColor.RED + throwable.getMessage());
                        } else {
                            sender.sendMessage(data.getKey() + "作成ヨシ！");
                        }
                    });
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(Stream.of("add", "remove", "list", "clear", "createAchievement"), args[0]).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static @NotNull Stream<String> filter(@NotNull Stream<String> stream, @NotNull String str) {
        return stream.filter(s -> s.toLowerCase(Locale.ROOT).startsWith(str.toLowerCase(Locale.ROOT)));
    }
}
