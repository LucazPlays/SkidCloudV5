package de.terrarier.multithreading.executables.proxy;

import de.terrarier.file.FileManager;
import de.terrarier.Wrapper;
import de.terrarier.server.proxy.ProxyGroup;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class SetupProxy implements Runnable {

    private final ProxyGroup group;

    public SetupProxy(ProxyGroup proxyGroup) {
        this.group = proxyGroup;
    }

    @Override
    public void run() {
        final int id = this.group.getNextId();
        final String path = Wrapper.getInstance().getProxyManager().getPath(this.group.isDynamic(), this.group.getName(), id);
        deleteIfRequired(path, this.group.isDynamic());
        new File(path + "/plugins").mkdirs();
        try {
            FileManager.copy("./Wrapper/Files/BungeeCord.jar", path + "/BungeeCord.jar");
            FileManager.copy("./Wrapper/Files/BungeeCloudApi.jar", path + "/plugins/BungeeCloudApi.jar");
            FileManager.copy("./Wrapper/Files/config.yml", path + "/config.yml");
           // FileManager.copy("./Wrapper/Files/ViaVersion.jar", path + "/plugins/ViaVersion.jar");
            // TODO: Fix file copying (from plugins folder)
            for (File all : new File("./Wrapper/Templates/Proxy/" + this.group.getName()).listFiles()) {
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
            final File[] files = new File("./Wrapper/GlobalTemplate/Proxy/").listFiles();
            if (files != null && files.length != 0) {
                for (File all : files) {
                    if (!all.isDirectory()) {
                        Files.copy(Paths.get(all.getAbsolutePath()),
                                Paths.get(path + all.getAbsolutePath().replaceAll("./Wrapper/GlobalTemplate/Proxy/", "")),
                                StandardCopyOption.REPLACE_EXISTING);
                    }else {
                        FileManager.copyFullDirectory(all.getAbsolutePath(), path + all.getAbsolutePath().replaceAll("./Wrapper/GlobalTemplate/Proxy/", ""));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!group.isDynamic()) {
            Logger.log("Der ProxyServer " + group.getName() + "-" + id + " wurde erstellt!",
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
