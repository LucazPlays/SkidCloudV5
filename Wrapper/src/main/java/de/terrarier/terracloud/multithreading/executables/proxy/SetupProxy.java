package de.terrarier.terracloud.multithreading.executables.proxy;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.file.FileUtil;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.proxy.ProxyGroup;

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
            FileUtil.copy("./Wrapper/Files/BungeeCord.jar", path + "/BungeeCord.jar");
            FileUtil.copy("./Wrapper/Files/BungeeCloudApi.jar", path + "/plugins/BungeeCloudApi.jar");
            FileUtil.copy("./Wrapper/Files/config.yml", path + "/config.yml");
            final File templateDir = new File("./Wrapper/Templates/Proxy/" + this.group.getName());
            if(templateDir.exists()) {
                final File[] files = templateDir.listFiles();
                if(files != null && files.length != 0) {
                    for (File all : files) {
                        if (!all.isDirectory()) {
                            new File(path + "/" + all.getName()).createNewFile();
                            Files.copy(Paths.get(all.getAbsolutePath()),
                                    Paths.get(path + "/" + all.getName()),
                                    StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            final File toCreate = new File(path + "/" + all.getName());
                            if (toCreate.exists()) {
                                final File[] toCreateFiles = toCreate.listFiles();
                                if (toCreateFiles != null && toCreateFiles.length != 0) {
                                    FileUtil.deleteDir(toCreate);
                                }
                            }
                            FileUtil.copyFullDirectory(all.getAbsolutePath(), toCreate.getAbsolutePath());
                        }
                    }
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
                        FileUtil.copyFullDirectory(all.getAbsolutePath(), path + all.getAbsolutePath().replaceAll("./Wrapper/GlobalTemplate/Proxy/", ""));
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
        File[] files;
        if (checkFile.exists() && (files = checkFile.listFiles()) != null && files.length > 0) {
            FileUtil.deleteDir(checkFile);
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        File dir = new File(path);
        if (dir.exists()) {
            FileUtil.deleteDir(dir);
        }
    }

}
