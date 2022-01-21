package net.azisaba.lobby.util;

import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.config.ServerInfo;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServerChecker implements Runnable {
    private final AzisabaLobby plugin;

    public ServerChecker(AzisabaLobby plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 20, 200);
    }

    @Override
    public void run() {
        Set<String> serversToCheck = getServersToCheck();
        for (String server : serversToCheck) {
            Util.requestPlayerCount(plugin, Util.random(new ArrayList<>(Bukkit.getOnlinePlayers())), server);
        }
    }

    public Set<String> getServersToCheck() {
        Set<String> servers = new HashSet<>();
        for (ServerInfo server : plugin.getServers().values()) {
            servers.addAll(server.getCountedServers());
        }
        return servers;
    }
}
