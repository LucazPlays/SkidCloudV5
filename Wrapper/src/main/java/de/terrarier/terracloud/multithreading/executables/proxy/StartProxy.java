package de.terrarier.terracloud.multithreading.executables.proxy;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketStartingInstance;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.utils.PortUtil;

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
        final String path = Wrapper.getInstance().getProxyManager().getPath(group.isDynamic(), group.getName(), id);
        if(!new File(path).exists()) {
            new SetupProxy(group).run();
        }
        final int port = PortUtil.freePort();

        Logger.log("Der ProxyServer " + group.getName() + "-" + id + " wird nun auf dem Port[" + port
                + "] gestartet...", LogType.INFO);
        final ProcessBuilder process = new ProcessBuilder(Arrays.asList("nice", "-n", "19", "java",
                "-Dname=p_" + this.group.getName() + "-" + this.id, "-Djline.terminal=jline.UnsupportedTerminal",
                "-Dfile.encoding=UTF-8", "-Dio.netty.leakDetectionLevel=DISABLED", "-Xms" + this.group.getMemory() + "M",
                "-Xmx" + this.group.getMemory() + "M", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-XX:MaxPermSize=256M",
                "-XX:+HeapDumpOnOutOfMemoryError", "-jar",
                path + "/BungeeCord.jar"));
        process.directory(new File(path));
        process.redirectErrorStream();
        try {
            group.addLocalServer(id, process.start(), port);
            Wrapper.getInstance().broadcastPacket(new PacketStartingInstance(group.getName(), id, true));
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("An issue occurred while attempting to start the proxyserver \"" + this.group.getName() + "-" + this.id + "\"!", LogType.CRITICAL);
        }
    }

}
