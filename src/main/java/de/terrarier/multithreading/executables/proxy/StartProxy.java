package de.terrarier.multithreading.executables.proxy;

import de.terrarier.lib.SimpleUUID;
import de.terrarier.Wrapper;
import de.terrarier.terracloud.packet.PacketStartingInstance;
import de.terrarier.server.proxy.ProxyGroup;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.utils.PortUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class StartProxy implements Runnable {

    private final ProxyGroup group;
    private final int id;

    public StartProxy(ProxyGroup group) {
        this(group, group.getNextId());
    }

    public StartProxy(ProxyGroup group, int id) {
        this.group = group;
        this.id = id;
    }

    @Override
    public void run() {
        final String path = Wrapper.getInstance().getProxyManager().getPath(this.group.isDynamic(), this.group.getName(), this.id);
        if(!new File(path).exists()) {
            new SetupProxy(this.group).run();
        }
        final int port = PortUtil.freePort();

        /*
        try {
            FileManager.setLine(10, "  host: 0.0.0.0:" + port, path + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        Logger.log("Der ProxyServer " + this.group.getName() + "-" + this.id + " wird nun auf dem Port[" + port
                + "] gestartet...", LogType.INFO);
        final ProcessBuilder process = new ProcessBuilder(Arrays.asList("java",
                "-Dname=p_" + this.group.getName() + "-" + this.id, "-Djline.terminal=jline.UnsupportedTerminal",
                "-Dfile.encoding=UTF-8", "-Dio.netty.leakDetectionLevel=DISABLED", "-Xms" + this.group.getMemory() + "M",
                "-Xmx" + this.group.getMemory() + "M", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-XX:MaxPermSize=256M",
                "-XX:+HeapDumpOnOutOfMemoryError", "-jar",
                path + "/BungeeCord.jar"));
        process.directory(new File(path));
        process.redirectErrorStream();
        try {
            final SimpleUUID uuid = Wrapper.getInstance().getServerManager().generateUUID();
            this.group.addServer(process.start(), uuid, this.id, port);
            Wrapper.getInstance().broadcastPacket(new PacketStartingInstance(this.group.getName(), this.id, true));
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("An issue occoured while attemping to start the proxyserver \"" + this.group.getName() + "-" + this.id + "\"!", LogType.CRITICAL);
        }
    }

}
