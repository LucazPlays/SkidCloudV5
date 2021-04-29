package de.terrarier.terracloud.multithreading.executables.spigot;

import de.terrarier.netlistening.Connection;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.file.FileUtil;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketStartInstance;
import de.terrarier.terracloud.networking.PacketUnregister;
import de.terrarier.terracloud.networking.ServiceType;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.ServerGroup;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public final class StopServer implements Runnable {

    private final BukkitServer server;
    private final boolean preventRestart;

    public StopServer(BukkitServer server) {
        this(server, false);
    }

    public StopServer(BukkitServer server, boolean preventRestart) {
        this.server = server;
        this.preventRestart = preventRestart;
    }

    @Override
    public void run() {
        final ServerGroup group = this.server.getGroup();
        final int id = this.server.getId();
        group.removeServer(id);
        final String path = Wrapper.getInstance().getServerManager().getPath(group.isDynamic(), group.getName(), id);
        if(group.isDynamic()) {
            if(Wrapper.getInstance().getSetting().isSaveServerLogs()) {
                File logs = new File(path + "/logs/");
                File[] files = logs.listFiles();
                if(logs.exists() && logs.isDirectory() && files != null && files.length != 0) {
                    try {
                        FileUtil.copy(files[0].getAbsolutePath(), "./Wrapper/ServerLogs/" + group.getName() + "-" + id + "-" + System.currentTimeMillis());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            FileUtil.deleteDir(new File(path));
        }else {
            File logDir = new File("./Wrapper/Servers/" + group.getName() + "/" + group.getName() + "-" + id + "/logs");
            if(logDir.exists() && logDir.isDirectory()) {
                File[] logs = logDir.listFiles();
                if(logs != null && logs.length > 0) {
                    for(File log : logs) {
                        if(log.lastModified() + 28800000L < System.currentTimeMillis()) {
                            log.delete();
                        }
                    }
                }
            }
        }
        if(server instanceof LocalServerInstance) {
            if(!preventRestart) {
                Wrapper.getInstance().getShutdownController().process(((LocalServerInstance<?>) server).getProcess(), () -> {
                    final PacketUnregister unregisterPacket = new PacketUnregister(this.server.getGroup().getName(),
                            ServiceType.MINECRAFT, this.server.getId());
                    Wrapper.getInstance().broadcastPacket(unregisterPacket);
                    Wrapper.getInstance().sendToMaster(unregisterPacket);
                    Logger.log("Der Server " + group.getName() + "-" + this.server.getId() + " wurde gestoppt!", LogType.INFO);
                    if(group.getServers().size() < group.getServerCount()) {
                        Wrapper.getInstance().getExecutorService().executeDelayed(() -> Wrapper.getInstance().sendToMaster(new PacketStartInstance(group.getName(), id,
                                false)), 250L, TimeUnit.MILLISECONDS);
                    }
                }, 5000L);
                final Connection connection = ((LocalServerInstance<?>) server).getConnection();
                if(connection.isConnected()) {
                    connection.disconnect();
                }
                try {
                    final OutputStream outputStream = ((LocalServerInstance<?>) server).getProcess().getOutputStream();
                    outputStream.write("stop\n".getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                ((LocalServerInstance<?>) server).getProcess().destroy();
                final PacketUnregister unregisterPacket = new PacketUnregister(this.server.getGroup().getName(),
                        ServiceType.MINECRAFT, this.server.getId());
                Wrapper.getInstance().broadcastPacket(unregisterPacket);
                if(!Wrapper.getInstance().isShutDown()) {
                    Wrapper.getInstance().sendToMaster(unregisterPacket);
                }
                Logger.log("Der Server " + group.getName() + "-" + this.server.getId() + " wurde gestoppt!", LogType.INFO);
            }
        }
    }

}
