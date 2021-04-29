package de.terrarier.terracloud.server.spigot;

import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.server.GroupInstance;

public final class ServerGroup extends GroupInstance<BukkitServer> {

    private final ServerVersion defaultServerVersion;

    public ServerGroup(String name, int serverCount, boolean dynamic, int memory) {
        this(name, serverCount, dynamic, memory, null);
    }

    public ServerGroup(String name, int serverCount, boolean dynamic, int memory, ServerVersion defaultServerVersion) {
        super(name, serverCount, dynamic, memory);
        this.defaultServerVersion = defaultServerVersion != null ? defaultServerVersion : ServerVersion.V1_8_8;
    }

    public BukkitServer addLocalServer(Process process, int id, int port) {
        return addLocalServer(process, id, port, defaultServerVersion);
    }

    public BukkitServer addLocalServer(Process process, int id, int port, ServerVersion serverVersion) {
        final BukkitServer server = new LocalBukkitServerImpl(process, this, id, port, serverVersion);
        addServer(server);
        return server;
    }

    public BukkitServer addServer(int id) {
        final BukkitServer server = new BukkitServerImpl(this, id);
        addServer(server);
        return server;
    }

    public ServerVersion getDefaultServerVersion() {
        return defaultServerVersion;
    }

}
