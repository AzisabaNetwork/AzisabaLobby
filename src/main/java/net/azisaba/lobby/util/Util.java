package net.azisaba.lobby.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class Util {
    private static final Random RANDOM = new Random();

    public static void requestPlayerCount(Plugin plugin, Player player, String server) {
        if (player == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public static void requestConnect(Plugin plugin, Player player, String server) {
        if (server == null) return;
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
        if (list.isEmpty()) return null;
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

    public static boolean isClassPresent(
            @NotNull
            @Language(value = "JAVA", prefix = "class X{void x(){Class.forName(\"", suffix = "\");}}")
            String className
    ) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static @NotNull Material findAny(@NotNull String @NotNull ... materials) {
        for (String material : materials) {
            Material mat = Material.matchMaterial(material);
            if (mat != null) return mat;
        }
        return Material.AIR;
    }
}
