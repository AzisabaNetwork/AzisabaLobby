package net.azisaba.lobby.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Defines how a player is sent to a game server.
 */
public enum ConnectionMethod {
    /**
     * Sends the {@code Connect} sub channel on the {@code BungeeCord} plugin message channel and lets the proxy
     * (BungeeCord / Velocity) move the player. The target is identified by a proxy-registered server name.
     */
    PROXY,

    /**
     * Sends a transfer packet (Minecraft 1.20.5+) so that the client reconnects to the given host and port directly.
     * The receiving server must have {@code accepts-transfers=true}.
     */
    TRANSFER;

    /**
     * Parses a configuration value.
     *
     * @param value the raw configuration value. May be null.
     * @return the matching method, or null if the value is null, blank or unknown.
     */
    public static @Nullable ConnectionMethod parse(@Nullable String value) {
        if (value == null) return null;
        final String normalized = value.trim();
        if (normalized.isEmpty()) return null;
        try {
            return valueOf(normalized.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Parses a configuration value, falling back to {@link #PROXY} when the value is not specified or is invalid.
     *
     * @param value the raw configuration value. May be null.
     * @return the parsed method. Never null.
     */
    public static @NotNull ConnectionMethod fromConfig(@Nullable String value) {
        final @Nullable ConnectionMethod parsed = parse(value);
        return parsed == null ? PROXY : parsed;
    }
}
