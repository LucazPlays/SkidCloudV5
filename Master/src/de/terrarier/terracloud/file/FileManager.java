package de.terrarier.terracloud.file;

import de.terrarier.terracloud.MasterSetting;
import de.terrarier.terracloud.lib.DBSetting;
import de.terrarier.terracloud.lib.SettingParser;
import de.terrarier.terracloud.server.proxy.BungeeSetting;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public final class FileManager {

    public static MasterSetting execute(boolean reload) throws IOException {
        final boolean firstStarted = !new File("./Master/").exists();
        if(!reload) {
            if (firstStarted) {
                new File("./Master/Logs/").mkdirs();
                new File("./Master/Logs/Log.txt").createNewFile();
            }
            System.setErr(new PrintStream(new FileOutputStream("./Master/Logs/Log.txt")));
        }
        if (firstStarted) {
            createInitialFiles();
        }
        final SettingParser settingParser = new SettingParser(new File("./Master/Config/config.txt"));
        final int masterPort = settingParser.getSetting("MasterPort", 21200);
        final int startPort = settingParser.getSetting("StartPort", 40001);
        final int portRange = settingParser.getSetting("PortRange", 998);
        final SettingParser dbSettingParser = new SettingParser(new File("./Master/Config/database.txt"));

        final DBSetting dbSetting = new DBSetting(dbSettingParser.getSetting("user", null),
                dbSettingParser.getSetting("password", null),
                dbSettingParser.getSetting("host", "localhost"),
                dbSettingParser.getSetting("port", 27017),
                dbSettingParser.getSetting("database", "default"));

        final SettingParser bungeeSettingParser = new SettingParser(new File("./Master/Config/bungeeconfig.txt"));
        final BungeeSetting bungeeSetting = new BungeeSetting(bungeeSettingParser.getSetting("Motd1", ""),
                bungeeSettingParser.getSetting("Motd2", ""),  bungeeSettingParser.getSetting("Slots", 100));
        try (BufferedReader reader = new BufferedReader(new FileReader("./Master/tmp/AuthKey.txt"))) {
            return new MasterSetting(firstStarted, masterPort, startPort, portRange, dbSetting, bungeeSetting,
                    UUID.fromString(reader.readLine()));
        }
    }

    private static void createInitialFiles() throws IOException {
        new File("./Master/Config/").mkdirs();
        new File("./Master/Templates/Server/").mkdirs();
        new File("./Master/Templates/Proxy/").mkdir();
        new File("./Master/GlobalTemplate/Server/").mkdirs();
        new File("./Master/GlobalTemplate/Proxy/").mkdir();
        new File("./Master/tmp").mkdir();
        write("./Master/tmp/AuthKey.txt", UUID.randomUUID().toString());
    }

    private static void write(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes());
    }

}
