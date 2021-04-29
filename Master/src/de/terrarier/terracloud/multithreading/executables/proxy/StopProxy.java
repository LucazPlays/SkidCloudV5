package de.terrarier.terracloud.multithreading.executables.proxy;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketStopInstance;
import de.terrarier.terracloud.networking.PacketUnregister;
import de.terrarier.terracloud.networking.ServiceType;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyServer;

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
        if(!preventRestart && group.getServerCount() > group.getServers().size()) {
            Master.getInstance().getExecutorService().executeDelayed(() -> new StartProxy(group).run(), 1000, TimeUnit.MILLISECONDS);
        }
        server.getWrapper().sendPacket(new PacketStopInstance(server.getGroup().getName(), server.getId(), true));
        Master.getInstance().broadcastPacket(new PacketUnregister(this.server.getGroup().getName(), ServiceType.PROXY, this.server.getId()));
        Logger.log("Der ProxyServer " + group.getName() + "-" + this.server.getId() + " wurde gestoppt!", LogType.INFO);
    }

}
