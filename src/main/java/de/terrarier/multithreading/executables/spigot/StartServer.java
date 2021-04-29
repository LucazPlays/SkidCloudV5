package de.terrarier.multithreading.executables.spigot;

import de.terrarier.file.FileManager;
import de.terrarier.lib.ServerVersion;
import de.terrarier.lib.SettingParser;
import de.terrarier.lib.SimpleUUID;
import de.terrarier.Wrapper;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.terracloud.packet.PacketStartingInstance;
import de.terrarier.server.spigot.ServerGroup;
import de.terrarier.utils.CallbackReference;
import de.terrarier.utils.PortUtil;

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
        }else if(this.group.isDynamic()) {
            FileManager.clearDirectory(file);
            new SetupServer(group, id, serverVersion).run();
        }
        final int port = PortUtil.freePort();
        Logger.log("Der Server " + this.group.getName() + "-" + this.id + " wird nun auf dem Port[" + port
                + "] gestartet...", LogType.INFO);
        try {
            //FileManager.setLine(24, "server-port=" + port, path + "/server.properties");
            SettingParser parser = new SettingParser(new File(path + "/server.properties"), "=");
            FileManager.setLine(parser.getSettingLineIndex("server-port"), "server-port=" + port, path + "/server.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ProcessBuilder process = new ProcessBuilder(Arrays.asList("java",
                "-Dname=s_" + this.group.getName() + "-" + this.id, "-Djline.terminal=jline.UnsupportedTerminal",
                "-Dfile.encoding=UTF-8", "-Dio.netty.leakDetectionLevel=DISABLED", "-Xms" + this.group.getMemory() + "M",
                "-Xmx" + this.group.getMemory() + "M", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-XX:MaxPermSize=256M",
                "-XX:+HeapDumpOnOutOfMemoryError", "-jar",
                path + "/Spigot.jar"));
        process.directory(file);
        process.redirectErrorStream();
        try {
            final SimpleUUID uuid = Wrapper.getInstance().getServerManager().generateUUID();
            this.group.addServer(process.start(), uuid, this.id, port);
            if(callbackReference != null) {
                callbackReference.triggerCallback(id);
            }
            Wrapper.getInstance().broadcastPacket(new PacketStartingInstance(this.group.getName(), this.id, false));
           // CustomPayloadUtil.sendGlobalMessage("§aDer Server §e" + this.group.getName() + "-" + this.id + " §awurde gestartet!", "cloud.seeupdate");
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("An issue occoured while attemping to start the server \"" + this.group.getName() + "-" + this.id + "\"!", LogType.CRITICAL);
        }
    }

}
