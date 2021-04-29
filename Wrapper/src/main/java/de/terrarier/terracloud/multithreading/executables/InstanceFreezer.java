package de.terrarier.terracloud.multithreading.executables;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.LocalBukkitServerImpl;
import de.terrarier.terracloud.server.spigot.ServerGroup;

public final class InstanceFreezer implements Runnable {

    @Override
    public void run() {
        for(ServerGroup group : Wrapper.getInstance().getServerManager().getGroups().values()) {
            for (BukkitServer server : group.getServers()) {
                if (server.isStarted() && server instanceof LocalServerInstance) {
                    final LocalBukkitServerImpl localServer = (LocalBukkitServerImpl) server;
                    if (!localServer.isFroze()) {
                        if (localServer.lastQuit() + 120000L < System.currentTimeMillis()
                                && server.getPlayers() < 1
                                && group.isDynamic()) {
                            if (localServer.doesFreeze()) {
                                localServer.freeze();
                                return;
                            }
                        } else {
                            localServer.resetFreezeCount();
                        }
                    }
                }
            }
        }
    }

}
