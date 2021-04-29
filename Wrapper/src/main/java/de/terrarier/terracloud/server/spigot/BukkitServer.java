package de.terrarier.terracloud.server.spigot;

import de.terrarier.terracloud.server.ServerInstance;

public interface BukkitServer extends ServerInstance<ServerGroup> {

    int getPort();

}
