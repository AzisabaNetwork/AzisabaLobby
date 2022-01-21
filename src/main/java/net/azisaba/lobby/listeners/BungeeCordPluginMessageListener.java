package net.azisaba.lobby.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import net.azisaba.lobby.AzisabaLobby;
import net.azisaba.lobby.gui.ServerSelectionScreen;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

@RequiredArgsConstructor
public class BungeeCordPluginMessageListener implements PluginMessageListener {
    private final AzisabaLobby plugin;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord") && !channel.equals("bungeecord:main")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equals("PlayerCount")) {
            String server = in.readUTF();
            int playerCount = in.readInt();
            ServerSelectionScreen.PLAYER_COUNT.put(server, playerCount);
            plugin.getServerSelectionScreen().update();
        }
    }
}
