package de.terrarier.terracloud.file;

import de.terrarier.terracloud.lib.ServerVersion;

import java.io.File;

public interface DownloadManager {

    String JAR_FILE_EXTENSION = ".jar";

    File downloadServerVersion(String name, String link);

    String getLink(ServerVersion serverVersion);

    File getServerVersionFile(ServerVersion serverVersion);

}
