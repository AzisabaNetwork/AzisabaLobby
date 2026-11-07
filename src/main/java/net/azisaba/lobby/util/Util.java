package net.azisaba.lobby.util;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public class Util {
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

    /**
     * Parses a {@code host:port} string used as a transfer destination.
     * <p>
     * IPv6 addresses must be wrapped in brackets, e.g. {@code [::1]:25565}. The returned address is unresolved, so the
     * host name is sent to the client as-is and DNS resolution happens on the client side.
     *
     * @param value the raw configuration value. May be null.
     * @return the parsed address, or null if the value is not a valid {@code host:port}.
     */
    public static @Nullable InetSocketAddress parseHostAndPort(@Nullable String value) {
        if (value == null) return null;
        final String trimmed = value.trim();
        if (trimmed.isEmpty()) return null;

        final String host;
        final String portString;
        if (trimmed.startsWith("[")) {
            final int closingBracket = trimmed.indexOf(']');
            if (closingBracket < 0) return null;
            if (closingBracket + 1 >= trimmed.length() || trimmed.charAt(closingBracket + 1) != ':') return null;
            host = trimmed.substring(1, closingBracket);
            portString = trimmed.substring(closingBracket + 2);
            if (host.isEmpty()) return null;
        } else {
            final int separator = trimmed.lastIndexOf(':');
            if (separator < 0) return null;
            host = trimmed.substring(0, separator);
            portString = trimmed.substring(separator + 1);
            // An unbracketed IPv6 address is ambiguous, so reject it instead of guessing.
            if (host.isEmpty() || host.indexOf(':') >= 0) return null;
        }

        if (!isValidHost(host) || !isValidPort(portString)) return null;
        return InetSocketAddress.createUnresolved(host, Integer.parseInt(portString));
    }

    private static boolean isValidHost(final @NotNull String host) {
        for (int i = 0; i < host.length(); i++) {
            if (Character.isWhitespace(host.charAt(i))) return false;
        }
        return true;
    }

    private static boolean isValidPort(final @NotNull String portString) {
        if (portString.isEmpty() || portString.length() > 5) return false;
        for (int i = 0; i < portString.length(); i++) {
            if (!Character.isDigit(portString.charAt(i))) return false;
        }
        final int port = Integer.parseInt(portString);
        return port >= 1 && port <= 65535;
    }
}
