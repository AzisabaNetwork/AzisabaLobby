package net.azisaba.lobby.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilTest {
    @Test
    void parsesHostAndPort() {
        final InetSocketAddress address = Util.parseHostAndPort("game.example.com:25565");
        assertNotNull(address);
        assertEquals("game.example.com", address.getHostString());
        assertEquals(25565, address.getPort());
        // The host name is sent to the client, so it must not be resolved on the server side.
        assertEquals("game.example.com", address.getHostName());
    }

    @Test
    void parsesBracketedIpv6() {
        final InetSocketAddress address = Util.parseHostAndPort("[::1]:25565");
        assertNotNull(address);
        assertEquals("::1", address.getHostString());
        assertEquals(25565, address.getPort());
    }

    @Test
    void parsesSurroundingWhitespace() {
        final InetSocketAddress address = Util.parseHostAndPort("  game.example.com:25565  ");
        assertNotNull(address);
        assertEquals("game.example.com", address.getHostString());
        assertEquals(25565, address.getPort());
    }

    @Test
    void parsesLowerBoundPort() {
        final InetSocketAddress address = Util.parseHostAndPort("localhost:1");
        assertNotNull(address);
        assertEquals(1, address.getPort());
    }

    @Test
    void parsesUpperBoundPort() {
        final InetSocketAddress address = Util.parseHostAndPort("localhost:65535");
        assertNotNull(address);
        assertEquals(65535, address.getPort());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "localhost",
            "localhost:",
            ":25565",
            "localhost:0",
            "localhost:65536",
            "localhost:99999",
            "localhost:abc",
            "localhost:-1",
            "localhost:25 65",
            "::1:25565",
            "[::1]25565",
            "[::1]:",
            "[]:25565",
            "localhost:25565:1",
            " bad host:25565",
    })
    void rejectsInvalidValues(final String value) {
        assertNull(Util.parseHostAndPort(value));
    }

    @Test
    void rejectsNull() {
        assertNull(Util.parseHostAndPort(null));
    }
}
