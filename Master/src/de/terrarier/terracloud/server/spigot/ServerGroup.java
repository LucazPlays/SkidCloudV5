package de.terrarier.terracloud.server.spigot;

import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.wrapper.Wrapper;

public final class ServerGroup extends GroupInstance<BukkitServer> {

    private final ServerVersion defaultServerVersion;

    public ServerGroup(String name, int serverCount, boolean dynamic, int memory) {
        this(name, serverCount, dynamic, memory, null);
    }

    public ServerGroup(String name, int serverCount, boolean dynamic, int memory, ServerVersion defaultServerVersion) {
        super(name, serverCount, dynamic, memory);
        this.defaultServerVersion = defaultServerVersion != null ? defaultServerVersion : ServerVersion.V1_8_8;
    }

    public BukkitServer addServer(int id, Wrapper wrapper) {
        return addServer(id, wrapper, defaultServerVersion);
    }

    public BukkitServer addServer(int id, Wrapper wrapper, ServerVersion serverVersion) {
        final BukkitServer server = new BukkitServer(this, id, wrapper, serverVersion);
        addServer(server);
        return server;
    }

    public ServerVersion getDefaultServerVersion() {
        return defaultServerVersion;
    }
}
