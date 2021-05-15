package de.terrarier.terracloud.file;

import de.terrarier.terracloud.Bootstrap;
import de.terrarier.terracloud.WrapperSetting;
import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.lib.SettingParser;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public final class FileManager {

    private DownloadManager downloadManager = new SpigotDownloadManager();

    public static WrapperSetting execute(boolean reload, FileManager fileManager) throws IOException {
        final boolean firstStarted = !new File("./Wrapper/").exists();
        if(!reload) {
            if (firstStarted) {
                new File("./Wrapper/Logs/").mkdirs();
                new File("./Wrapper/Logs/Log.txt").createNewFile();
            } else {
                FileUtil.clearDirectory(new File("./Wrapper/Temp/"));
            }
            System.setErr(new PrintStream(new FileOutputStream("./Wrapper/Logs/Log.txt")));
        }
        if(firstStarted) {
            createInitialFiles();
            fileManager.downloadFiles();
        }
        final SettingParser settingParser = new SettingParser(new File("./Wrapper/Config/config.txt"));
        final String name = settingParser.getSetting("Name", "Wrapper-1");
        final boolean saveServerLogs = settingParser.getSetting("SaveServerLogs", false);
        final String masterHost = settingParser.getSetting("MasterHost", "127.0.0.1");
        final int masterPort = settingParser.getSetting("MasterPort", 20465);
        final int wrapperPort = settingParser.getSetting("WrapperPort", 21050);
        final int startPort = settingParser.getSetting("StartPort", 40001);
        final int portRange = settingParser.getSetting("PortRange", 998);

        try (BufferedReader reader = new BufferedReader(new FileReader("./Wrapper/Files/tmp/AuthKey.txt"))) {
            return new WrapperSetting(name, firstStarted, saveServerLogs, masterHost, masterPort, wrapperPort, startPort,
                    portRange, UUID.fromString(reader.readLine()));
        }catch (Exception ex) {
            Logger.log("Please insert the AuthKey.txt file into the Wrapper/Files/tmp folder.", LogType.CRITICAL);
            Bootstrap.shutdown();
            return null;
        }
    }

    public static void createInitialFiles() throws IOException {
        new File("./Wrapper/Config/").mkdirs();
        new File("./Wrapper/Proxys/").mkdir();
        new File("./Wrapper/Files/PrivateServerPlugins/").mkdirs();
        new File("./Wrapper/Files/tmp/").mkdir();
        new File("./Wrapper/Files/Spigot").mkdir();
        new File("./Wrapper/Servers/").mkdir();
        new File("./Wrapper/Templates/Server/").mkdirs();
        new File("./Wrapper/Templates/Proxy/").mkdir();
        new File("./Wrapper/Temp/Servers/").mkdirs();
        new File("./Wrapper/Temp/Proxys/").mkdir();
        new File("./Wrapper/GlobalTemplate/Server/").mkdirs();
        new File("./Wrapper/GlobalTemplate/Proxy/").mkdir();
        new File("./Wrapper/ServerLogs/").mkdir();
        write("./Wrapper/Files/eula.txt", "eula=true");
        write("./Wrapper/Files/server.properties", "spawn-protection=16\r\n" + "generator-settings=\r\n" + "force-gamemode=false\r\n"
                + "allow-nether=true\r\n" + "gamemode=0\r\n" + "broadcast-console-to-ops=true\r\n"
                + "enable-query=false\r\n" + "player-idle-timeout=0\r\n" + "difficulty=1\r\n"
                + "spawn-monsters=true\r\n" + "op-permission-level=1\r\n" + "resource-pack-hash=\r\n"
                + "announce-player-achievements=false\r\n" + "pvp=true\r\n" + "snooper-enabled=true\r\n"
                + "level-type=DEFAULT\r\n" + "hardcore=false\r\n" + "enable-command-block=false\r\n"
                + "max-players=100\r\n" + "network-compression-threshold=256\r\n"
                + "max-world-size=29999984\r\n" + "server-port=25565\r\n" + "debug=false\r\n"
                + "server-ip=127.0.0.1\r\n" + "spawn-npcs=true\r\n" + "allow-flight=false\r\n"
                + "level-name=world\r\n" + "view-distance=10\r\n" + "resource-pack=\r\n"
                + "spawn-animals=true\r\n" + "white-list=false\r\n" + "generate-structures=true\r\n"
                + "online-mode=false\r\n" + "max-build-height=256\r\n" + "level-seed=\r\n"
                + "motd=A server\r\n" + "enable-rcon=false");
        write("./Wrapper/Files/config.yml", "player_limit: -1\r\n" + "ip_forward: true\r\n" + "permissions:\r\n" + "  default:\r\n"
                + "  - example.example\r\n" + "  admin:\r\n" + "  - bungeecord.command.alert\r\n"
                + "  - bungeecord.command.end\r\n" + "  - bungeecord.command.ip\r\n"
                + "  - bungeecord.command.reload\r\n" + "timeout: 30000\r\n" + "log_commands: false\r\n"
                + "online_mode: true\r\n" + "servers:\r\n" + "  Lobby-1:\r\n" + "    motd: '&1Lobby-1'\r\n"
                + "    address: localhost:40010\r\n" + "    restricted: false\r\n" + "listeners:\r\n"
                + "- query_port: 25577\r\n" + "  motd: '&1Another Bungee server'\r\n" + "  priorities:\r\n"
                + "  - Lobby-1\r\n" + "  bind_local_address: true\r\n" + "  tab_list: SERVER\r\n"
                + "  query_enabled: false\r\n" + "  host: 0.0.0.0:25565\r\n" + "  forced_hosts:\r\n"
                + "    pvp.md-5.net: pvp\r\n" + "  max_players: 100\r\n" + "  tab_size: 60\r\n"
                + "  ping_passthrough: false\r\n" + "  force_default_server: true\r\n"
                + "  proxy_protocol: false\r\n" + "disabled_commands:\r\n" + "- disabledcommandhere\r\n"
                + "network_compression_threshold: 256\r\n" + "groups:\r\n" + "  md_5:\r\n"
                + "  - admin\r\n" + "connection_throttle: 4000\r\n" + "connection_throttle_limit: 3\r\n"
                + "stats: f2876aa6-74d2-468c-90ee-1377111f1c9f\r\n" + "forge_support: false\r\n"
                + "inject_commands: false");
        write("./Wrapper/Files/spigot.yml", "config-version: 8\r\n" + "settings:\r\n"
                + "  save-user-cache-on-stop-only: false\r\n" + "  bungeecord: true\r\n"
                + "  late-bind: false\r\n" + "  sample-count: 12\r\n" + "  player-shuffle: 0\r\n"
                + "  filter-creative-items: true\r\n" + "  user-cache-size: 1000\r\n"
                + "  int-cache-limit: 1024\r\n" + "  moved-wrongly-threshold: 0.0625\r\n"
                + "  moved-too-quickly-threshold: 100.0\r\n" + "  timeout-time: 60\r\n"
                + "  restart-on-crash: false\r\n" + "  restart-script: ./start.sh\r\n"
                + "  netty-threads: 4\r\n" + "  attribute:\r\n" + "    maxHealth:\r\n" + "      max: 2048.0\r\n"
                + "    movementSpeed:\r\n" + "      max: 2048.0\r\n" + "    attackDamage:\r\n"
                + "      max: 2048.0\r\n" + "  debug: false\r\n" + "commands:\r\n" + "  tab-complete: 0\r\n"
                + "  log: true\r\n" + "  spam-exclusions:\r\n" + "  - /skill\r\n"
                + "  silent-commandblock-console: false\r\n" + "  replace-commands:\r\n" + "  - setblock\r\n"
                + "  - summon\r\n" + "  - testforblock\r\n" + "  - tellraw\r\n" + "messages:\r\n"
                + "  whitelist: You are not whitelisted on this server!\r\n"
                + "  unknown-command: Unknown command. Type \"/help\" for help.\r\n"
                + "  server-full: The server is full!\r\n"
                + "  outdated-client: Outdated client! Please use {0}\r\n"
                + "  outdated-server: Outdated server! I'm still on {0}\r\n"
                + "  restart: Server is restarting\r\n" + "stats:\r\n" + "  disable-saving: false\r\n"
                + "  forced-stats: {}\r\n" + "world-settings:\r\n" + "  default:\r\n" + "    verbose: true\r\n"
                + "    mob-spawn-range: 4\r\n" + "    anti-xray:\r\n" + "      enabled: true\r\n"
                + "      engine-mode: 1\r\n" + "      hide-blocks:\r\n" + "      - 14\r\n" + "      - 15\r\n"
                + "      - 16\r\n" + "      - 21\r\n" + "      - 48\r\n" + "      - 49\r\n" + "      - 54\r\n"
                + "      - 56\r\n" + "      - 73\r\n" + "      - 74\r\n" + "      - 82\r\n" + "      - 129\r\n"
                + "      - 130\r\n" + "      replace-blocks:\r\n" + "      - 1\r\n" + "      - 5\r\n"
                + "    nerf-spawner-mobs: false\r\n" + "    growth:\r\n" + "      cactus-modifier: 100\r\n"
                + "      cane-modifier: 100\r\n" + "      melon-modifier: 100\r\n"
                + "      mushroom-modifier: 100\r\n" + "      pumpkin-modifier: 100\r\n"
                + "      sapling-modifier: 100\r\n" + "      wheat-modifier: 100\r\n"
                + "      netherwart-modifier: 100\r\n" + "    entity-activation-range:\r\n"
                + "      animals: 32\r\n" + "      monsters: 32\r\n" + "      misc: 16\r\n"
                + "    entity-tracking-range:\r\n" + "      players: 48\r\n" + "      animals: 48\r\n"
                + "      monsters: 48\r\n" + "      misc: 32\r\n" + "      other: 64\r\n" + "    ticks-per:\r\n"
                + "      hopper-transfer: 8\r\n" + "      hopper-check: 8\r\n" + "    hopper-amount: 1\r\n"
                + "    random-light-updates: false\r\n" + "    save-structure-info: true\r\n"
                + "    max-bulk-chunks: 10\r\n" + "    max-entity-collisions: 8\r\n"
                + "    dragon-death-sound-radius: 0\r\n" + "    seed-village: 10387312\r\n"
                + "    seed-feature: 14357617\r\n" + "    hunger:\r\n" + "      walk-exhaustion: 0.2\r\n"
                + "      sprint-exhaustion: 0.8\r\n" + "      combat-exhaustion: 0.3\r\n"
                + "      regen-exhaustion: 3.0\r\n" + "    max-tnt-per-tick: 100\r\n" + "    max-tick-time:\r\n"
                + "      tile: 50\r\n" + "      entity: 50\r\n" + "    merge-radius:\r\n"
                + "      item: 2.5\r\n" + "      exp: 3.0\r\n" + "    item-despawn-rate: 6000\r\n"
                + "    arrow-despawn-rate: 1200\r\n" + "    enable-zombie-pigmen-portal-spawns: true\r\n"
                + "    wither-spawn-sound-radius: 0\r\n" + "    view-distance: 10\r\n"
                + "    hanging-tick-frequency: 100\r\n" + "    zombie-aggressive-towards-villager: true\r\n"
                + "    chunks-per-tick: 650\r\n" + "    clear-tick-list: false");
        Logger.log("Copying \"BungeeCloudApi.jar\"...", LogType.INFO);
        FileUtil.insertData("files/BungeeApi.jar", "./Wrapper/Files/BungeeCloudApi.jar");
        Logger.log("Copied file successfully.", LogType.INFO);
        Logger.log("Copying \"CloudApi.jar\"...", LogType.INFO);
        FileUtil.insertData("files/CloudApi.jar", "./Wrapper/Files/CloudApi.jar");
        Logger.log("Copied file successfully.", LogType.INFO);
    }

    private static void write(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes(/*StandardCharsets.UTF_8*/));
    }

    public void downloadFiles() {
        Logger.log("skidding \"Spigot.jar\"...", LogType.INFO);
        downloadManager.getServerVersionFile(ServerVersion.V1_8_8);
        Logger.log("skid successful.", LogType.INFO);
        Logger.log("skidding \"BungeeCord.jar\"...", LogType.INFO);
        File bungeeCordFile = new File("./Wrapper/Files", "BungeeCord.jar");
        try {
            bungeeCordFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("An error occurred while skidding \"BungeeCord.jar\"!", LogType.ERROR);
        }
        FileUtil.downloadFileFromURL(
                "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar",
                bungeeCordFile);
        Logger.log("skidding successful.", LogType.INFO);
    }

    public void setDownloadManager(DownloadManager manager) {
        downloadManager = manager;
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

}
