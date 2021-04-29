package de.terrarier.multithreading.executables.spigot;

import de.terrarier.file.FileManager;
import de.terrarier.lib.ServerVersion;
import de.terrarier.Wrapper;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.server.spigot.ServerGroup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class SetupServer implements Runnable {

    private final ServerGroup group;
    private final int id;
    private final ServerVersion serverVersion;

    // TODO: Add server version!

    public SetupServer(ServerGroup group, int id, ServerVersion serverVersion) {
        this.group = group;
        this.id = id;
        if(serverVersion == null) {
            serverVersion = group.getDefaultServerVersion();
        }
        this.serverVersion = serverVersion;
    }

    @Override
    public void run() {
        final String path = Wrapper.getInstance().getServerManager().getPath(this.group.isDynamic(), this.group.getName(), this.id);
        deleteIfRequired(path, this.group.isDynamic());
        new File(path + "/plugins").mkdirs();
        try {
            FileManager.copy(FileManager.getDownloadManager().getServerVersionFile(serverVersion).getAbsolutePath()/*"./Wrapper/Files/Spigot.jar"*/, path + "/Spigot.jar");
            FileManager.copy("./Wrapper/Files/eula.txt", path + "/eula.txt");
            FileManager.copy("./Wrapper/Files/server.properties", path + "/server.properties");
            FileManager.copy("./Wrapper/Files/spigot.yml", path + "/spigot.yml");
            // FileManager.copy("./Wrapper/Files/ViaVersion.jar", path + "/plugins/ViaVersion.jar");
            File[] templateContent = new File("./Wrapper/Templates/Server/" + this.group.getName()).listFiles();
            if (templateContent != null) {
                for (File all : templateContent) {
                    if (!all.isDirectory()) {
                        new File(path + "/" + all.getName()).createNewFile();
                        Files.copy(Paths.get(all.getAbsolutePath()),
                                Paths.get(path + "/" + all.getName()),
                                StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        File toCreate = new File(path + "/" + all.getName());
                        if (toCreate.exists()) {
                            if (toCreate.listFiles().length != 0) {
                                FileManager.deleteDir(toCreate);
                            }
                        }
                        FileManager.copyFullDirectory(all.getAbsolutePath(), toCreate.getAbsolutePath());
                    }
                }
        }
        final File[] files = new File("./Wrapper/GlobalTemplate/Server/").listFiles();
        if (files != null && files.length != 0) {
            for (File all : files) {
                if (!all.isDirectory()) {
                    String[] pathParts = all.getAbsolutePath().split("\\\\Wrapper\\\\GlobalTemplate\\\\Server\\\\");
                    if(pathParts.length < 2) {
                        pathParts = all.getAbsolutePath().split("/Wrapper/GlobalTemplate/Server/");
                    }
                    Files.copy(Paths.get(all.getAbsolutePath()),
                            Paths.get(path + "/" + pathParts[1]),
                            StandardCopyOption.REPLACE_EXISTING);
                    /*
                        Files.copy(Paths.get(all.getAbsolutePath()),
                                Paths.get(path + all.getAbsolutePath().replaceAll("./Wrapper/GlobalTemplate/Server/", "")),
                                StandardCopyOption.REPLACE_EXISTING);
                    */
                }else {
                    String[] pathParts = all.getAbsolutePath().split("\\\\Wrapper\\\\GlobalTemplate\\\\Server\\\\");
                    if(pathParts.length < 2) {
                        pathParts = all.getAbsolutePath().split("/Wrapper/GlobalTemplate/Server/");
                    }
                    FileManager.copyFullDirectory(all.getAbsolutePath(), path + "/" + pathParts[1]);
                   //FileManager.copyFullDirectory(all.getAbsolutePath(), path + all.getAbsolutePath().replaceAll("./Wrapper/GlobalTemplate/Server/", ""));
                }
            }
        }
        if(!new File(path + "/plugins/CloudApi.jar").exists()) {
            FileManager.copy("./Wrapper/Files/CloudApi.jar", path + "/plugins/CloudApi.jar");
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!this.group.isDynamic()) {
            Logger.log("Der Server " + this.group.getName() + "-" + this.id + " wurde erstellt!",
                    LogType.INFO);
        }
    }

    private void deleteIfRequired(String path, boolean dynamic) {
        if (!dynamic) {
            return;
        }
        File checkFile = new File(path + "/");
        /*if (checkFile.exists() && checkFile.listFiles() != null && checkFile.listFiles().length > 0) {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (checkFile.exists() && checkFile.listFiles() != null && checkFile.listFiles().length > 0) {
                FileManager.deleteDir(checkFile);
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/
        File[] files;
        if (checkFile.exists() && (files = checkFile.listFiles()) != null && files.length > 0) {
            FileManager.deleteDir(checkFile);
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        File dir = new File(path);
        if (dir.exists()) {
            //error in underlying line
            FileManager.deleteDir(dir);
        }
    }

}
