package de.terrarier.multithreading.executables.proxy;

import de.terrarier.Wrapper;
import de.terrarier.file.FileManager;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.terracloud.packet.PacketUnRegister;
import de.terrarier.server.proxy.ProxyGroup;
import de.terrarier.server.proxy.ProxyServer;

import java.io.File;
import java.util.concurrent.TimeUnit;

public final class StopProxy implements Runnable {

    private final ProxyServer server;
    private final boolean preventRestart;

    public StopProxy(ProxyServer server) {
        this(server, false);
    }

    public StopProxy(ProxyServer server, boolean preventRestart) {
        this.server = server;
        this.preventRestart = preventRestart;
    }

    @Override
    public void run() {
        final ProxyGroup group = this.server.getGroup();
        final int id = this.server.getId();
        group.removeServer(id);
        final String path = Wrapper.getInstance().getProxyManager().getPath(group.isDynamic(), group.getName(), id);
        if(group.isDynamic()) {
            FileManager.deleteDir(new File(path));
        }
        if(!this.preventRestart && group.getServerCount() >= id) {
            //                                                                                                 1, TimeUnit.SECONDS
            Wrapper.getInstance().getExecutorService().executeDelayed(() -> new StartProxy(group).run(), 250, TimeUnit.MILLISECONDS);
        }
       // ProcessManager.executeAndDestroyProcess(new ProcessBuilder(Arrays.asList("pkill", "-9", "-f", "p_" + group.getName() + "-" + id)));
        this.server.getProcess().destroyForcibly();
        Logger.log("Der ProxyServer " + group.getName() + "-" + this.server.getId() + " wurde gestoppt!", LogType.INFO);
        Wrapper.getInstance().broadcastPacket(new PacketUnRegister(this.server.getGroup().getName(), this.server.getId()));
    }

}
