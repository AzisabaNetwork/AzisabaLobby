# AzisabaLobby

## Permission

- `azisabalobby.allow-interact-flower-pot`
  - Permissions related to the destruction of flowerpots.
  - default: `op`
- `azisabalobby.allow-destroy-painting`
  - Permissions related to the destruction of picture frames.
  - default: `op`
- `azisabalobby.allow-creative-mode`
  - Whether the player is allowed to be in creative mode.
  - default: `op`
- `azisabalobby.allow-survival-mode`
  - Whether the player is allowed to be in survival mode.
  - default: `op`
- `azisabalobby.allow-spectator-mode`
  - Whether the player is allowed to be in spectator mode.
  - default: `op`
- `azisabalobby.allow-flight`
  - Whether the player is allowed to fly.
  - default: `op`

## Connection methods

Each entry under `servers` can define how a player is sent to the game with the `connectionMethod` option.

### `proxy` (default)

Sends the `Connect` sub channel on the `BungeeCord` plugin message channel, so the proxy (BungeeCord / Velocity) moves
the player. The destination is a server name registered in the proxy, and `servers` must contain at least one entry.

```yaml
servers:
  '0':
    name: "&aCool server"
    servers:
    - coolserver1
    - coolserver2
    connectionMethod: proxy
```

### `transfer`

Sends a transfer packet (Minecraft 1.20.5+) so that the client reconnects to `transferAddress` directly. The value must
be `host:port`; IPv6 addresses must be wrapped in brackets (`[::1]:25565`). `servers` is not required because the game
has a single destination, and the client resolves the host name.

```yaml
servers:
  '1':
    name: "&aDirect transfer game"
    servers: []
    connectionMethod: transfer
    transferAddress: "game.example.com:25565"
```

Notes:

- The receiving server must accept transfers: set `accepts-transfers=true` in `server.properties` for Paper / vanilla,
  and `advanced.accepts-transfers=true` in `velocity.toml` when the destination is behind Velocity. Otherwise the client
  is disconnected after the transfer.
- A transfer packet only carries a host and port, so proxy server names (such as those used by `proxy`) cannot be used as
  the destination.
- If the proxy uses modern forwarding or a similar authentication scheme, transferring a client directly to a backend
  server will usually fail authentication. Transfer to an endpoint that accepts direct connections instead.
- Unlike `proxy`, the player count of a `transfer` game is read from the proxy via `countedServers`. Leave
  `countedServers` empty for a standalone destination, and the count stays `0`.
- `selectableServers` and `selectOnly` are ignored for `transfer` games, and a warning is logged.
- If `transferAddress` is missing or invalid, or `Player#transfer` throws `IllegalStateException`, the plugin shows an
  error to the player and logs a warning. It does not fall back to the proxy connection.
