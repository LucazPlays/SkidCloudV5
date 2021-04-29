package de.terrarier.file;

import de.terrarier.WrapperSetting;
import de.terrarier.lib.ServerVersion;
import de.terrarier.lib.SettingParser;
import de.terrarier.lib.SqlSetting;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.server.proxy.BungeeSetting;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class FileManager {

    public static final String DEFAULT_PATH = new File("none").getAbsolutePath().replaceAll("none", "");
    private static DownloadManager downloadManager = new SpigotDownloadManager();

    public static WrapperSetting execute(boolean reload) throws IOException {
        final boolean firstStarted = !new File("./Wrapper/").exists();
        boolean saveServerLogs = false;
        String masterHost;
        int masterPort = 21200;
        int wrapperPort = 21050;
        int startPort = 40001;
        int portRange = 998;
        SqlSetting sqlSetting;
        BungeeSetting bungeeSetting;
        if(!reload) {
            if (firstStarted) {
                new File("./Wrapper/Logs/").mkdirs();
                new File("./Wrapper/Logs/Log.txt").createNewFile();
            } else {
                clearDirectory(new File("./Wrapper/Temp/"));
            }
            System.setErr(new PrintStream(new FileOutputStream("./Wrapper/Logs/Log.txt")));
        }
        if (firstStarted) {
            masterHost = "127.0.0.1";
            sqlSetting = new SqlSetting("localhost", "root", "123", "default", 3306);
            bungeeSetting = new BungeeSetting("None", "None");
            createInitialFiles();
            downloadFiles();
        } else {
            final SettingParser settingParser = new SettingParser(new File("./Wrapper/Config/config.txt"));
            saveServerLogs = Boolean.parseBoolean(settingParser.getSetting("SaveServerLogs", "false"));
            masterHost = settingParser.getSetting("MasterHost", "127.0.0.1");
            masterPort = Integer.parseInt(settingParser.getSetting("MasterPort", "21200"));
            wrapperPort = Integer.parseInt(settingParser.getSetting("WrapperPort", "21050"));
            startPort = Integer.parseInt(settingParser.getSetting("StartPort", "40001"));
            portRange = Integer.parseInt(settingParser.getSetting("PortRange", "998"));
            final SettingParser sqlSettingParser = new SettingParser(new File("./Wrapper/Config/sql.txt"));

            sqlSetting = new SqlSetting(sqlSettingParser.getSetting("host", "localhost"),
                    sqlSettingParser.getSetting("user", "root"), sqlSettingParser.getSetting("password", null),
                    sqlSettingParser.getSetting("database", "default"),
                    Integer.parseInt(sqlSettingParser.getSetting("port", "3306")));

            final SettingParser bungeeSettingParser = new SettingParser(new File("./Wrapper/Config/bungeeconfig.txt"));
            bungeeSetting = new BungeeSetting(bungeeSettingParser.getSetting("Motd1", ""), bungeeSettingParser.getSetting("Motd2", ""));
        }
        return new WrapperSetting(firstStarted, saveServerLogs, masterHost, masterPort, wrapperPort, startPort, portRange, sqlSetting, bungeeSetting);
    }

    public static void copy(String src, String destination) throws IOException {
        final Path srcPath = Paths.get(src);
        final Path dstPath = Paths.get(destination);
        final File file = new File(destination);
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void createInitialFiles() throws IOException {
        new File("./Wrapper/Config/").mkdirs();
        new File("./Wrapper/Proxys/").mkdir();
        new File("./Wrapper/Files/PrivateServerPlugins/").mkdirs();
        new File("./Wrapper/Files/tmp/").mkdir();
        new File("./Wrapper/Files/Spigot").mkdir();
        new File("./Wrapper/Servers/").mkdirs();
        new File("./Wrapper/Templates/Server/").mkdirs();
        new File("./Wrapper/Templates/Proxy/").mkdir();
        new File("./Wrapper/Temp/Servers/").mkdirs();
        new File("./Wrapper/Temp/Proxys/").mkdir();
        new File("./Wrapper/GlobalTemplate/Server/").mkdirs();
        new File("./Wrapper/GlobalTemplate/Proxy/").mkdir();
        new File("./Wrapper/ServerLogs/").mkdir();
        new File("./Wrapper/Config/sql.txt").createNewFile();
        new File("./Wrapper/Files/eula.txt").createNewFile();
        new File("./Wrapper/Files/server.properties").createNewFile();
        new File("./Wrapper/Files/config.yml").createNewFile();
        new File("./Wrapper/Config/config.txt").createNewFile();
        new File("./Wrapper/Files/spigot.yml").createNewFile();
        new File("./Wrapper/Config/bungeeconfig.txt");
        PrintWriter pw = new PrintWriter("./Wrapper/Config/sql.txt", "UTF-8");
        pw.print("host: localhost\r\n" + "user: root\r\n" + "password: 123\r\n"
                + "database: default\r\n" + "port: 3306\r\n");
        pw.close();
        pw = new PrintWriter("./Wrapper/Files/eula.txt", "UTF-8");
        pw.println("eula=true");
        pw.close();
        pw = new PrintWriter("./Wrapper/Files/server.properties", "UTF-8");
        pw.println("spawn-protection=16\r\n" + "generator-settings=\r\n" + "force-gamemode=false\r\n"
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
                + "motd=A Server\r\n" + "enable-rcon=false");
        pw.close();
        pw = new PrintWriter("./Wrapper/Files/config.yml", "UTF-8");
        pw.write("player_limit: -1\r\n" + "ip_forward: true\r\n" + "permissions:\r\n" + "  default:\r\n"
                + "  - system.msg\r\n" + "  admin:\r\n" + "  - bungeecord.command.alert\r\n"
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
                + "network_compression_threshold: 256\r\n" + "groups:\r\n" + "  terrarier2111:\r\n"
                + "  - admin\r\n" + "connection_throttle: 4000\r\n" + "connection_throttle_limit: 3\r\n"
                + "stats: f2876aa6-74d2-468c-90ee-1377111f1c9f\r\n" + "forge_support: false\r\n"
                + "inject_commands: false");
        pw.close();
        pw = new PrintWriter("./Wrapper/Config/config.txt", "UTF-8");
        pw.write(
                "SaveServerLogs: false\r\nMasterHost: 127.0.0.1\r\nMasterPort: 21200\r\nWrapperPort: 21050\r\nStartPort: 40001\r\nPortRange: 998");
        pw.close();
        pw = new PrintWriter("./Wrapper/Config/bungeeconfig.txt", "UTF-8");
        pw.write(
                "Motd1: None\r\nMotd2: None");
        pw.close();
        pw = new PrintWriter("./Wrapper/Files/spigot.yml", "UTF-8");
        pw.write("config-version: 8\r\n" + "settings:\r\n"
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
                + "  whitelist: Du befindest dich nicht auf der Whitelist dieses Servers!\r\n"
                + "  unknown-command: Unbekannter Befehl! Nutze \"/help\" f�r Hilfe.\r\n"
                + "  server-full: Der Server ist voll!\r\n"
                + "  outdated-client: Veralteter client! Bitte nutze {0}\r\n"
                + "  outdated-server: Veralteter Server! Wir sind immer noch auf der {0}\r\n"
                + "  restart: Server restartet!\r\n" + "stats:\r\n" + "  disable-saving: false\r\n"
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
        pw.close();
        Logger.log("Copying \"BungeeCloudApi.jar\"...", LogType.INFO);
        // insertData("files/BungeeCloudApi.jar", "Wrapper/Files/BungeeCloudApi.jar");
        insertData("files/BungeeCloudApi.jar", "./Wrapper/Files/BungeeCloudApi.jar");
        Logger.log("Copied file successfully.", LogType.INFO);
        Logger.log("Copying \"CloudApi.jar\"...", LogType.INFO);
        insertData("files/CloudApi.jar", "./Wrapper/Files/CloudApi.jar");
        Logger.log("Copied file successfully.", LogType.INFO);
    }

    public static void insertData(String src, String destination) {
        File file = new File(destination);
        file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream localInputStream = FileManager.class.getClassLoader().getResourceAsStream(src);
            Throwable localThrowable3 = null;
            try {
                if (localInputStream != null) {
                    Files.copy(localInputStream, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Throwable localThrowable1) {
                localThrowable3 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (localInputStream != null) {
                    if (localThrowable3 != null) {
                        try {
                            localInputStream.close();
                        } catch (Throwable localThrowable2) {
                            localThrowable3.addSuppressed(localThrowable2);
                        }
                    } else {
                        localInputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try (InputStream localInputStream = FileManager.class.getClassLoader().getResourceAsStream(src)) {
            if (localInputStream != null) {
                Files.copy(localInputStream, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFiles() {
        Logger.log("Downloading \"Spigot.jar\"...", LogType.INFO);
       /* final File spigotFile = new File("./Wrapper/Files", "Spigot.jar");
        try {
            spigotFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("An error occurred while downloading \"Spigot.jar\"!", LogType.ERROR);
        }
        downloadFileFromURL("https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar", spigotFile);*/
        downloadManager.getServerVersionFile(ServerVersion.V1_8_8);
        Logger.log("Download successful.", LogType.INFO);
        Logger.log("Downloading \"BungeeCord.jar\"...", LogType.INFO);
        File bungeeCordFile = new File("./Wrapper/Files", "BungeeCord.jar");
        try {
            bungeeCordFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("An error occurred while downloading \"BungeeCord.jar\"!", LogType.ERROR);
        }
        downloadFileFromURL(
                "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar",
                bungeeCordFile);
        Logger.log("Download successful.", LogType.INFO);
        /*
        Logger.log("Downloading \"ViaVersion.jar\"...", LogType.INFO);
        File viaFile = new File("./Wrapper/Files", "ViaVersion.jar");
        try {
            viaFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("An error occurred while downloading \"ViaVersion.jar\"!", LogType.ERROR);
        }
        // https://ci.viaversion.com/job/ViaVersion/352/artifact/jar/target/ViaVersion-2.1.2.jar
        // https://ci.viaversion.com/job/ViaVersion/lastSuccessfulBuild/artifact/jar/target/ViaVersion-2.1.0.jar
        // https://ci.viaversion.com/job/ViaVersion/lastSuccessfulBuild/artifact/jar/target/ViaVersion-3.1.1-SNAPSHOT.jar
        // 339068, 305488
        // https://www.spigotmc.org/resources/viaversion.19254/download?version=350151
        // https://ci.viaversion.com/job/ViaVersion/lastSuccessfulBuild/artifact/jar/target/ViaVersion-3.2.0-SNAPSHOT.jar
        //downloadFileFromURL("https://ci.viaversion.com/job/ViaVersion/467/artifact/jar/target/ViaVersion-3.2.0-SNAPSHOT.jar", viaFile);
        //downloadFileFromURL("http://myles.us/ViaVersion/latest.jar", viaFile);
        downloadFileFromURL("https://www.spigotmc.org/resources/viaversion.19254/download?version=356163", viaFile);
        Logger.log("Download successful.", LogType.INFO);
        */
    }

    public static void downloadFileFromURL(String urlString, File destination) {
        try {
            final URL website = new URL(urlString);
            final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            final FileOutputStream fos = new FileOutputStream(destination);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setLine(int lineNumber, String data, String filePath) throws IOException {
        final Path path = Paths.get(filePath);
        final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lineNumber > lines.size()) {
            return;
        }
        lines.set(lineNumber, data);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static void addLine(int lineBefore, String text, String filePath) throws IOException {
        final Path path = Paths.get(filePath);
        final List<String> lines = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new FileReader(filePath));
        int index = 0;
        String s;
        while ((s = reader.readLine()) != null) {
            if (index++ == lineBefore - 1) {
                lines.add(text + "\r\n");
            }
            lines.add(s);
        }
        reader.close();
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static void removeLine(int lineNumber, String filePath) throws IOException {
        final Path path = Paths.get(filePath);
        final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        lines.remove(lineNumber);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private static final Object LOCK = new Object();

    public static void deleteDir(File dir) {
        try {
            if (dir == null || !dir.exists()) {
                return;
            }
            synchronized (LOCK) {
                File[] files;
                if (dir.isDirectory() && (files = dir.listFiles()) != null && files.length != 0) {
                    File[] subFiles = dir.listFiles();
                    for (File f : subFiles) {
                        if (f.isDirectory()) {
                            deleteDir(f);
                        } else {
                            f.delete();
                        }
                    }
                } else {
                    dir.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFullDirectory(String from, String to) {
        File dst = new File(to);
        if (!dst.exists()) {
            dst.mkdirs();
        }
        File src = new File(from);
        File[] files = src.listFiles();
        if (files != null) {
            if (files.length != 0) {
                for (File all : files) {
                    if (!all.isDirectory()) {
                        Path p1 = Paths.get(all.getAbsolutePath());
                        Path p2 = Paths.get(to + "/" + all.getName());
                        try {
                            Files.copy(p1, p2, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        copyFullDirectory(all.getAbsolutePath(), to + "/" + all.getName());
                    }
                }
            }
        }
    }

    /*public static void prepareAllVersions() {
        Logger.log("Preparing all Versions...", LogType.INFO);
        downloadSpigot("1_8_8", "https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar");
        downloadSpigot("1_9", "https://cdn.getbukkit.org/spigot/spigot-1.9-R0.1-SNAPSHOT-latest.jar");
        downloadSpigot("1_9_2", "https://cdn.getbukkit.org/spigot/spigot-1.9.2-R0.1-SNAPSHOT-latest.jar");
        downloadSpigot("1_9_4", "https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar");
        downloadSpigot("1_10", "https://cdn.getbukkit.org/spigot/spigot-1.10-R0.1-SNAPSHOT-latest.jar");
        downloadSpigot("1_10_2", "https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar");
        downloadSpigot("1_11", "https://cdn.getbukkit.org/spigot/spigot-1.11.jar");
        downloadSpigot("1_11_1", "https://cdn.getbukkit.org/spigot/spigot-1.11.1.jar");
        downloadSpigot("1_11_2", "https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar");
        downloadSpigot("1_12", "https://cdn.getbukkit.org/spigot/spigot-1.12.jar");
        downloadSpigot("1_12_1", "https://cdn.getbukkit.org/spigot/spigot-1.12.1.jar");
        downloadSpigot("1_12_2", "https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar");
        Logger.log("Prepared all Versions!", LogType.INFO);
    }*/

    public static void clearDirectory(File dir) {
        File[] files = dir.listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    public static void setDownloadManager(DownloadManager manager) {
        downloadManager = manager;
    }

    public static DownloadManager getDownloadManager() {
        return downloadManager;
    }

}
