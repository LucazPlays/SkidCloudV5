package de.terrarier.terracloud.multithreading.executables.proxy;

import de.terrarier.netlistening.Connection;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.file.FileUtil;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketStartInstance;
import de.terrarier.terracloud.networking.PacketStopInstance;
import de.terrarier.terracloud.networking.PacketUnregister;
import de.terrarier.terracloud.networking.ServiceType;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
            FileUtil.deleteDir(new File(path));
        }
        if(server instanceof LocalServerInstance) {
            if(!preventRestart) {
                Wrapper.getInstance().getShutdownController().process(((LocalServerInstance<?>) server).getProcess(), () -> {
                    final PacketUnregister unregister = new PacketUnregister(this.server.getGroup().getName(), ServiceType.PROXY, this.server.getId());
                    Wrapper.getInstance().broadcastPacket(unregister);
                    Wrapper.getInstance().sendToMaster(unregister);
                    Logger.log("Der ProxyServer " + group.getName() + "-" + this.server.getId() + " wurde gestoppt!", LogType.INFO);
                    if(group.getServers().size() < group.getServerCount()) {
                        Wrapper.getInstance().getExecutorService().executeDelayed(() -> Wrapper.getInstance().sendToMaster(new PacketStartInstance(group.getName(), id,
                                true)), 250, TimeUnit.MILLISECONDS);
                    }
                }, 5000L);
                final Connection connection = ((LocalServerInstance<?>) server).getConnection();
                if(connection.isConnected()) {
                    connection.disconnect();
                }
                try {
                    final OutputStream outputStream = ((LocalServerInstance<?>) server).getProcess().getOutputStream();
                    outputStream.write("end\n".getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                ((LocalServerInstance<?>) server).getProcess().destroy();
                final PacketUnregister unregister = new PacketUnregister(this.server.getGroup().getName(), ServiceType.PROXY, this.server.getId());
                Wrapper.getInstance().broadcastPacket(unregister);
                if(!Wrapper.getInstance().isShutDown()) {
                    Wrapper.getInstance().sendToMaster(unregister);
                }
                Logger.log("Der ProxyServer " + group.getName() + "-" + this.server.getId() + " wurde gestoppt!", LogType.INFO);
            }
        }else {
            Wrapper.getInstance().sendToMaster(new PacketStopInstance(group.getName(), id, true));
        }
    }

}
