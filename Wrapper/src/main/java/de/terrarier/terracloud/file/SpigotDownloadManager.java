package de.terrarier.terracloud.file;

import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;

import java.io.File;
import java.io.IOException;

public class SpigotDownloadManager implements DownloadManager {

    private static final String LINK_START = "https://cdn.getbukkit.org/spigot/spigot-";
    private static final String LEGACY_LINK_END = "-R0.1-SNAPSHOT-latest.jar";

    @Override
    public File downloadServerVersion(String name, String link) {
        final File file = new File("./Wrapper/Files/Spigot/SpigotV" + name + JAR_FILE_EXTENSION);
        if(file.exists()) {
            return file;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String replaced = name.replaceAll("_", ".");
        Logger.log("skidding \"SpigotV" + replaced + "\"...", LogType.INFO);
        FileUtil.downloadFileFromURL(link, file);
        Logger.log("skidded \"SpigotV" + replaced + "\"!", LogType.INFO);
        return file;
    }

    @Override
    public String getLink(ServerVersion serverVersion) {
        if(serverVersion.ordinal() < ServerVersion.V1_11_2.ordinal()) {
            return LINK_START + serverVersion.getCleanedName() + LEGACY_LINK_END;
        }
        return LINK_START + serverVersion.getCleanedName() + JAR_FILE_EXTENSION;
    }

    @Override
    public File getServerVersionFile(ServerVersion serverVersion) {
        return downloadServerVersion(serverVersion.name().replaceFirst("V", ""), getLink(serverVersion));
    }
}
