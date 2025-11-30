package net.azisaba.lobby.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.azisaba.lobby.AzisabaLobby;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class BungeeCordPluginMessageListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(final @NotNull String channel, final @NotNull Player player, byte[] message) {
        if (!channel.equals("BungeeCord") && !channel.equals("bungeecord:main")) {
            return;
        }

        final ByteArrayDataInput bytes = ByteStreams.newDataInput(message);
        final String subChannel = bytes.readUTF();

        if (subChannel.equals("PlayerCount")) {
            final String server = bytes.readUTF();
            int playerCount = bytes.readInt();
            AzisabaLobby.instance().gameMap().values().stream()
                    .filter(g -> g.getServers().contains(server))
                    .findFirst()
                    .ifPresent(game -> game.updatePlayerCount(server, playerCount));
        }
    }
}
