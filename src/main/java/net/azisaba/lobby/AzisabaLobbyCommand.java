package net.azisaba.lobby;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AzisabaLobbyCommand implements TabExecutor {
    private final AzisabaLobby plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /azisabalobby <reload>");
            return true;
        }
        if (sender.hasPermission("azisabalobby.reload") && args[0].equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded configuration.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0 || !sender.hasPermission("azisabalobby.command")) return Collections.emptyList();
        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            if (sender.hasPermission("azisabalobby.reload")) commands.add("reload");
            return filterCommands(commands, args[0]);
        }
        return Collections.emptyList();
    }

    private static List<String> filterCommands(List<String> list, String s) {
        return list.stream()
                .filter(s1 -> s1.toLowerCase(Locale.ROOT).startsWith(s.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }
}
