package net.azisaba.lobby.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Random;

public class Util {
    private static final Random RANDOM = new Random();

    public static void requestPlayerCount(Plugin plugin, Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public static void requestConnect(Plugin plugin, Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public static int clamp(int value, int minInclusive, int maxInclusive) {
        if (value < minInclusive) return minInclusive;
        return Math.min(value, maxInclusive);
    }

    public static <T> T random(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static String repeat(String s, int count) {
        if (count < 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
