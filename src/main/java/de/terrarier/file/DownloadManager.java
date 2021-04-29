package de.terrarier.file;

import de.terrarier.lib.ServerVersion;

import java.io.File;

public interface DownloadManager {

    String JAR_FILE_EXTENSION = ".jar";

    File downloadServerVersion(String name, String link);

    String getLink(ServerVersion serverVersion);

    File getServerVersionFile(ServerVersion serverVersion);

}
