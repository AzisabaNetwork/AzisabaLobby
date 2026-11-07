# AzisabaLobby: transfer パケットによるゲーム転送方式の追加

- 対象ブランチ: `develop`
- 状態: 実装済み / ビルド・単体テスト通過（未コミット）

## 背景 / 現状

- 現在の作業ツリーは `master`（Spigot 1.12.2 API、`ServerInfo` 構成）。実装対象は **`develop`**（`Game` モデル、Paper 1.21.11 / Java 25、Gradle 9.2.1）。
- `develop` の接続処理は `Game#tryConnection(...)` のみで、BungeeCord プラグインメッセージ `Connect`（Velocity/BungeeCord 内部転送）を送っている。
- Paper 1.21.11 API には `Player#transfer(String host, int port)` が存在する（`transferTo` ではない）。API ドキュメント上、転送できない状況では `IllegalStateException` を投げる。
- transfer パケットは host:port のみを運び、サーバー名は指定できない。受け側は:
  - Paper/vanilla: `server.properties` の `accepts-transfers=true` が必要（既定 false）
  - Velocity 経由: `velocity.toml` の `advanced.accepts-transfers=true` が必要。ただし転送後は Velocity の通常の初期サーバー選択になる（サーバー名指定は不可）
- クライアントは 1.20.5 以上が必要（本サーバーは 1.21.11 なので実質問題なし）。

## 決定事項（確認済み）

1. **`develop` ブランチに実装**（master は変更しない）
2. 転送先は **ゲームごとに 1 つの `host:port`**
3. 転送先未設定・不正・`IllegalStateException` 時は **エラー表示のみ。プロキシ Connect へのフォールバックはしない**

## 設定スキーマ（後方互換）

既存キーはそのまま。Game ごとに以下の任意キーを追加する。

```yaml
servers:
  '1':
    material: ENDER_EYE
    itemDamage: 0
    name: "&aParkour"
    servers: []                 # transfer 方式なら空でよい
    countedServers: []
    description:
    - "直接転送で接続するゲーム"
    status: "&a公開中"
    recommendedVersion: "1.21.11"
    connectionMethod: transfer  # proxy (既定) | transfer
    transferAddress: "parkour.example.com:25565"  # transfer 時は必須
```

- `connectionMethod` 未指定 → `proxy`（従来動作、警告なし）
- `connectionMethod` 不正値 → 警告ログを出して `proxy` にフォールバック（起動は継続）
- `transferAddress` は `host:port`。IPv6 は `[::1]:25565` 形式。DNS 解決はせず（クライアントにホスト名を渡すため）`InetSocketAddress.createUnresolved` を使う
- `connectionMethod: proxy` なのに `transferAddress` がある → 無視して警告
- `connectionMethod: transfer` なのに `transferAddress` がない/不正 → 読み込み時に警告。クリック時にエラー表示（プロキシ転送はしない）
- `transfer` なのに `selectOnly` / `selectableServers` が true → 無視して警告

## 実装ステップ

### 1. モデル層

**新規 `src/main/java/net/azisaba/lobby/model/ConnectionMethod.java`**
- `enum ConnectionMethod { PROXY, TRANSFER }`
- `static @Nullable ConnectionMethod parse(String)`（trim + 大文字化、不明値は null）
- `static @NotNull ConnectionMethod fromConfig(String)`（null/不明値は `PROXY`）

**`src/main/java/net/azisaba/lobby/model/Game.java`**
- フィールド追加（Lombok `@Data` のコンストラクタ末尾）:
  - `@NonNull ConnectionMethod connectionMethod`
  - `@Nullable InetSocketAddress transferAddress`
- 追加メソッド:
  - `boolean isTransfer()`
  - `boolean canAttemptConnection()` … `isTransfer() || !servers.isEmpty()`（transfer で `servers: []` でもクリック可能にする）
- `tryConnection(Player)` / `tryConnection(Player, String)` を分岐:
  - `TRANSFER` → 単一の `transferAddress` へ転送
  - `PROXY` → 従来どおり `Connect`（`servers` が空なら何もしない）
- 追加 private `tryTransfer(Player)`:
  - 転送先 null → 赤メッセージ「転送先が設定されていません…」+ `getLogger().warning`
  - `player.closeInventory()` 後に `player.transfer(address.getHostString(), address.getPort())`
  - `IllegalStateException` を catch → 赤メッセージ「転送に失敗しました…」+ 警告ログ（プレイヤー名・宛先付き）
- `toItemStack()`: transfer ゲームの lore に「(サーバーへ直接転送されます)」を追加（アドレスは表示しない）
- ついでに `tryConnection(player, server)` の `{;` を修正

**`src/main/java/net/azisaba/lobby/util/Util.java`**
- `static @Nullable InetSocketAddress parseHostAndPort(String)` を追加
  - trim、空文字・コロンなし・ホスト空・port 非数値/0/65535超・非ブラケット IPv6・空白入りホストを拒否

### 2. 設定読み込み

**`src/main/java/net/azisaba/lobby/AzisabaLobby.java`**
- `reload()` で `connectionMethod` / `transferAddress` を読み、`Util.parseHostAndPort` で変換
- `servers` 未指定時: 従来の `Collections.singletonList((String) map.get("server"))` は null 要素入りリストになるため、`server` も未指定なら `Collections.emptyList()`、null 要素は除外（`countedServers` も同様）
- `new Game(...)`（唯一の生成箇所）に新引数を追加
- 上記の設定警告（不正 method / 不正 address / transfer なのに address 無し / proxy なのに address 有り / selectOnly との併用）を追加
- クラス内の設定例コメントにも新キーを追記

### 3. GUI

**`src/main/java/net/azisaba/lobby/gui/GameMenuGui.java`**
- `onInventoryClick` 冒頭のガードを `game == null || !game.canAttemptConnection()` に変更
- クリック処理: transfer ゲームは左/右/Shift いずれでも `game.tryConnection(player)`。middle クリック（権限デバッグ）は何もしない
- `shouldOpenGameMenu`: `game.isTransfer()` なら常に false
- `gameItem` の lore: transfer の場合は「クリックしてプレイ！」を表示し、`selectOnly`/`selectableServers` 向け文言は出さない

**`src/main/java/net/azisaba/lobby/gui/GameGui.java`**
- 通常は transfer ゲームから開かれないため機能変更なし
- `serverSlots`（15枠）超過時の `ArrayIndexOutOfBoundsException` を防ぐ `break` ガードだけ追加

### 4. ドキュメント

- **`src/main/resources/config.yml`**: `connectionMethod` / `transferAddress` のコメント付き例を追加。`servers: # required` を「transfer 時は不要」に修正
- **`README.md`**: 「Connection methods」節を追加
  - `proxy`: BungeeCord/Velocity の `Connect`。転送先は Velocity に登録済みのサーバー名
  - `transfer`: 1.20.5+ の transfer パケット。クライアントが host:port へ直接再接続
  - 受け側の `accepts-transfers=true`（Paper/vanilla は `server.properties`、Velocity は `advanced.accepts-transfers`）を明記
  - プロキシ配下のバックエンドへ直接転送すると modern forwarding 等で失敗しうること、transfer パケットでは Velocity のバックエンドを指定できないこと
  - transfer ゲームのプレイヤー数は `countedServers` にプロキシ管理サーバーを書かない限り 0 になること
  - 失敗時にプロキシへフォールバックしない仕様

### 5. ビルド / テスト / CI

- **`build.gradle.kts`**: JUnit 5/6 のテスト基盤を追加（`junit-bom 6.1.3`, `junit-jupiter`, `junit-platform-launcher`, `tasks.test { useJUnitPlatform() }`）
- 新規テスト:
  - `src/test/java/net/azisaba/lobby/model/ConnectionMethodTest.java`
  - `src/test/java/net/azisaba/lobby/util/UtilTest.java`
- **`.github/workflows/build-test.yml`**: 現在 `Build with Java 8` / Java 17 のままで、develop の `VERSION_25` と不整合。`java-version: "25"` とジョブ名を更新
- 検証コマンド:
  ```
  export JAVA_HOME="$HOME/.gradle/jdks/eclipse_adoptium-25-aarch64-os_x.2/jdk-25.0.4.1+1/Contents/Home"
  sh ./gradlew --no-daemon build
  ```
  - リポジトリの `gradlew` は mode 100644 のため `sh ./gradlew` で実行（CI は `chmod +x` 済み）

## データフロー / 公開 API の変更

- `Game` のコンストラクタ引数が 2 つ増える（`ConnectionMethod`, `InetSocketAddress`）。生成箇所は `AzisabaLobby.reload()` の 1 か所のみ
- config スキーマは任意キー追加のみで後方互換
- ネットワークプロトコル/DB/コマンド/権限の変更なし
- `BungeeCordPluginMessageListener` / `PlayerCountService` は変更なし

## エッジケース / 失敗モード

- `servers`・`server` ともに未指定 → 空リスト。proxy モードでクリックしても NPE せず何もしない
- `servers` に null 要素 → 除去
- transfer 先未設定/不正 → 警告ログ + クリック時エラー表示、接続は行わない
- `transfer()` が `IllegalStateException` → クラッシュさせず、プレイヤーへエラー、ログに詳細
- 転送前にインベントリを閉じる（`GameGui`/`GameMenuGui` の更新タスク・リスナーが残らない）
- 同じサーバーへ transfer する設定ミス（ループ）は検出できないため README で注意喚起
- Velocity の `accepts-transfers` が false → クライアント側で切断される。プラグインから検知できないため事前設定が必要（ドキュメント化）

## 受け入れ基準

1. 新キーを書いていない既存 config は、従来と完全に同じ挙動（proxy Connect、人数表示、GUI 遷移）
2. `connectionMethod: transfer` + 有効な `transferAddress` のゲームは、どのサーバー項目をクリックしてもその host:port へ `Player#transfer` で転送される
3. `servers: []` の transfer ゲームがゲームメニューからクリックでき、転送される
4. 転送先未設定/不正時はプロキシ Connect にフォールバックせず、エラーメッセージとログ警告が出る
5. `IllegalStateException` でもサーバーが落ちない
6. transfer ゲームではサーバー選択 GUI が開かず、lore に直接転送であることが表示される
7. `./gradlew build`（JDK 25）が成功し、追加した単体テストが通る
8. README / config.yml に `accepts-transfers` などの前提が記載されている

## 前提 / スコープ外

- サーバー名ごとの個別アドレス対応（`servers` と host:port のマップ）は今回の決定により実装しない。将来必要なら `Game` にマップを足して拡張可能
- 失敗時のプロキシへの自動フォールバックは行わない
- Velocity / Paper 側の設定変更（`accepts-transfers` の有効化）はコード外。ドキュメントのみ
- transfer 先のプレイヤー数集計、ロードバランシング、クロスネットワークの認証連携は対象外
- `master` ブランチ（1.12.2 API）は変更しない

## 実装結果（2026-11-07 時点）

- `sh ./gradlew --no-daemon build`（JDK 25 / Gradle 9.2.1 / Paper 1.21.11）: **BUILD SUCCESSFUL**
- 単体テスト: 37 件すべて成功
  - `net.azisaba.lobby.model.ConnectionMethodTest`: 14 件
  - `net.azisaba.lobby.util.UtilTest`: 23 件
- 生成物: `build/libs/AzisabaLobby-2.0.0.jar`

### 未検証（実機が必要）

- 実際の transfer: 1.20.5+ クライアントと、受け側サーバーの `accepts-transfers=true` 設定が必要
- Velocity 配下での転送（`advanced.accepts-transfers` と forwarding 設定の組み合わせ）

### 変更ファイル一覧

```
.github/workflows/build-test.yml
README.md
build.gradle.kts
gradle/libs.versions.toml
src/main/java/net/azisaba/lobby/AzisabaLobby.java
src/main/java/net/azisaba/lobby/gui/GameGui.java
src/main/java/net/azisaba/lobby/gui/GameMenuGui.java
src/main/java/net/azisaba/lobby/model/Game.java
src/main/java/net/azisaba/lobby/model/ConnectionMethod.java   (新規)
src/main/java/net/azisaba/lobby/util/Util.java
src/main/resources/config.yml
src/test/java/net/azisaba/lobby/model/ConnectionMethodTest.java   (新規)
src/test/java/net/azisaba/lobby/util/UtilTest.java                 (新規)
```
