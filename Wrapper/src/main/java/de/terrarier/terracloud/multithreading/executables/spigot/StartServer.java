package de.terrarier.terracloud.multithreading.executables.spigot;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.file.FileUtil;
import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.lib.SettingParser;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketStartingInstance;
import de.terrarier.terracloud.server.spigot.ServerGroup;
import de.terrarier.terracloud.utils.CallbackReference;
import de.terrarier.terracloud.utils.OSType;
import de.terrarier.terracloud.utils.PortUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class StartServer implements Runnable {

    private final ServerGroup group;
    private final int id;
    private CallbackReference callbackReference;
    private ServerVersion serverVersion;

    public StartServer(ServerGroup group) {
    this(group, group.getNextId());
    }

    public StartServer(ServerGroup group, CallbackReference callbackReference) {
        this(group, group.getNextId());
        this.callbackReference = callbackReference;
    }

    public StartServer(ServerGroup group, int id) {
        this.group = group;
        this.id = id;
    }

    public StartServer(ServerGroup group, int id, ServerVersion serverVersion) {
        this.group = group;
        this.id = id;
        this.serverVersion = serverVersion;
    }

    @Override
    public void run() {
        final String path = Wrapper.getInstance().getServerManager().getPath(this.group.isDynamic(), this.group.getName(), this.id);
        final File file = new File(path);
        if(!file.exists()) {
            new SetupServer(group, id, serverVersion).run();
        }else if(group.isDynamic()) {
            FileUtil.clearDirectory(file);
            new SetupServer(group, id, serverVersion).run();
        }
        final int port = PortUtil.freePort();
        Logger.log("Der Server " + this.group.getName() + "-" + this.id + " wird nun auf dem Port[" + port
                + "] gestartet...", LogType.INFO);
        try {
            SettingParser parser = new SettingParser(new File(path + "/server.properties"), "=");
            FileUtil.setLine(parser.getSettingLineIndex("server-port"), "server-port=" + port, path + "/server.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ProcessBuilder process;
        // TODO: Implement native process api for linux!
        /*if(OSType.getOSType() == OSType.LINUX) {
            process = new ProcessBuilder(Arrays.asList("nice", "-n", "19", "java",
                    "-Dname=s_" + this.group.getName() + "-" + this.id, "-Djline.terminal=jline.UnsupportedTerminal",
                    "-Dfile.encoding=UTF-8", "-Dio.netty.leakDetectionLevel=DISABLED", "-Xms" + this.group.getMemory() + "M",
                    "-Xmx" + this.group.getMemory() + "M", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-XX:MaxPermSize=256M",
                    "-XX:+HeapDumpOnOutOfMemoryError", "-jar",
                    path + "/Spigot.jar"));
        }else {*/
            process = new ProcessBuilder(Arrays.asList("java",
                    "-Dname=s_" + this.group.getName() + "-" + this.id, "-Djline.terminal=jline.UnsupportedTerminal",
                    "-Dfile.encoding=UTF-8", "-Dio.netty.leakDetectionLevel=DISABLED", "-Xms" + this.group.getMemory() + "M",
                    "-Xmx" + this.group.getMemory() + "M", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-XX:MaxPermSize=256M",
                    "-XX:+HeapDumpOnOutOfMemoryError", /*"-nogui", */"-jar",
                    path + "/Spigot.jar"));
       // }
        process.directory(file);
        process.redirectErrorStream();
        try {
            group.addLocalServer(process.start(), id, port);
            if(callbackReference != null) {
                callbackReference.triggerCallback(id);
            }
            Wrapper.getInstance().broadcastPacket(new PacketStartingInstance(group.getName(), id, false));
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("An issue occurred while attempting to start the server \"" + group.getName() + "-" + this.id + "\"!", LogType.CRITICAL);
        }
    }

}
