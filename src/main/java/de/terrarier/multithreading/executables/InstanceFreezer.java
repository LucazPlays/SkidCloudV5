package de.terrarier.multithreading.executables;

import de.terrarier.Wrapper;
import de.terrarier.server.spigot.BukkitServer;
import de.terrarier.server.spigot.ServerGroup;

public final class InstanceFreezer implements Runnable {

    @Override
    public void run() {
        for(ServerGroup group : Wrapper.getInstance().getServerManager().getGroups().values()) {
            for (BukkitServer server : group.getServers()) {
                if (server.isStarted()) {
                    if (!server.isFroze()) {
                        if (server.lastQuit() + 120000L < System.currentTimeMillis()
                                && server.getPlayers() < 1
                                && group.isDynamic()) {
                            if (server.doesFreeze()) {
                                server.freeze();
                                return;
                            }
                        } else {
                            server.resetFreezeCount();
                        }
                    }
                }
            }
        }
    }

}
