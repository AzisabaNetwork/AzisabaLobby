package net.azisaba.lobby.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Skulls {
    public static @NotNull ItemStack azisaba() {
        return create("ewogICJ0aW1lc3RhbXAiIDogMTc2NDUxMjgzMTc5OCwKICAicHJvZmlsZUlkIiA6ICI0OThjYTc2ZGYwODM0NzhmOGY0NjdjOGY1OTQwMjk1MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJHdWx0cm8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM1OGI1YWRmOGVmZjgxZDI1NGNmYmI3MTAxMjFjZjM1NTE4M2E1OGVjMjJlZTRlM2UzNDUxMDFlZTcyNTUxOCIKICAgIH0KICB9Cn0=");
    }

    public static @NotNull ItemStack youTube() {
        return create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM1NmRkYmVmMWI4MWJkNTdjOWIxYzZkNWQxYjc4YjU0NzM3YjcxOTkyOWIyOWMyYTkzMGE1ZjdjMmFlNGE4NiJ9fX0=");
    }

    public static @NotNull ItemStack twitter() {
        return create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY4NWEwYmU3NDNlOTA2N2RlOTVjZDhjNmQxYmEyMWFiMjFkMzczNzFiM2Q1OTcyMTFiYjc1ZTQzMjc5In19fQ==");
    }

    public static @NotNull ItemStack discord() {
        return create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY2ZmRhODFhYTMwY2RkMjA3OWRiN2NjOTBkYWU2ZWUzNDZjZTRhYWJmOWU2YTg3ZjFmNTFhZWIxYTQ0MGQifX19");
    }

    public static @NotNull ItemStack gitHub() {
        return create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJjYTkxODVkN2E5MGYwN2VhYjM1MTBjYzFhZGJlYmQwNzViY2MyOTU4YWY5MjQ4NTAyMTUwYThjYjQyYTQ2MSJ9fX0=");
    }

    private static @NotNull ItemStack create(final @NotNull String textures) {
        final ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        final SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
        final PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID(), null);
        playerProfile.setProperty(new ProfileProperty("textures", textures));
        itemMeta.setPlayerProfile(playerProfile);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private Skulls() {
    }
}
