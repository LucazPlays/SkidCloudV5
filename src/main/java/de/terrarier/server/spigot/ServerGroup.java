package de.terrarier.server.spigot;

import de.terrarier.lib.ServerVersion;
import de.terrarier.lib.SimpleUUID;
import de.terrarier.server.GroupInstance;

public final class ServerGroup extends GroupInstance<BukkitServer> {

    private final ServerVersion defaultServerVersion;

    public ServerGroup(String name, int serverCount, boolean dynamic, int memory, ServerVersion defaultServerVersion) {
        super(name, serverCount, dynamic, memory);
        this.defaultServerVersion = defaultServerVersion != null ? defaultServerVersion : ServerVersion.V1_8_8;
    }

    public BukkitServer addServer(Process process, SimpleUUID uuid, int id, int port) {
        return addServer(process, uuid, id, port, defaultServerVersion);
    }

    public BukkitServer addServer(Process process, SimpleUUID uuid, int id, int port, ServerVersion serverVersion) {
        final BukkitServer server = new BukkitServer(process, this, uuid, id, port, serverVersion);
        addServer(server);
        return server;
    }

    public ServerVersion getDefaultServerVersion() {
        return defaultServerVersion;
    }
}
