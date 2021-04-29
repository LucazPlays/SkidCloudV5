package de.terrarier.terracloud.multithreading.executables.spigot;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketUnregister;
import de.terrarier.terracloud.networking.ServiceType;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.ServerGroup;

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
        if (!this.preventRestart && group.getServerCount() > group.getServers().size()) {
            Master.getInstance().getExecutorService().executeDelayed(() -> new StartServer(group).run(), 1000, TimeUnit.MILLISECONDS);
        }
        Logger.log("Der Server " + group.getName() + "-" + this.server.getId() + " wurde gestoppt!", LogType.INFO);
        Master.getInstance().broadcastPacket(new PacketUnregister(this.server.getGroup().getName(), ServiceType.MINECRAFT, this.server.getId()));
    }

}
