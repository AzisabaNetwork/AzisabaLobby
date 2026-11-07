package net.azisaba.lobby.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConnectionMethodTest {
    @Test
    void defaultsToProxyWhenNotSpecified() {
        assertEquals(ConnectionMethod.PROXY, ConnectionMethod.fromConfig(null));
        assertEquals(ConnectionMethod.PROXY, ConnectionMethod.fromConfig(""));
        assertEquals(ConnectionMethod.PROXY, ConnectionMethod.fromConfig("   "));
        assertEquals(ConnectionMethod.PROXY, ConnectionMethod.fromConfig("something-else"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"transfer", "TRANSFER", "Transfer", " transfer "})
    void parsesTransferIgnoringCaseAndWhitespace(final String value) {
        assertEquals(ConnectionMethod.TRANSFER, ConnectionMethod.fromConfig(value));
        assertEquals(ConnectionMethod.TRANSFER, ConnectionMethod.parse(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"proxy", "PROXY", " Proxy "})
    void parsesProxyIgnoringCaseAndWhitespace(final String value) {
        assertEquals(ConnectionMethod.PROXY, ConnectionMethod.fromConfig(value));
        assertEquals(ConnectionMethod.PROXY, ConnectionMethod.parse(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "unknown", "transferred", "proxy-ish"})
    void returnsNullForUnknownValues(final String value) {
        assertNull(ConnectionMethod.parse(value));
    }

    @Test
    void returnsNullForNullValue() {
        assertNull(ConnectionMethod.parse(null));
    }
}
