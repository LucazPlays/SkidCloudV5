package de.terrarier.terracloud.server.spigot;

import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.server.ServerInstance;

public final class BukkitServer extends ServerInstance<ServerGroup> {

	private final ServerVersion serverVersion;

	public BukkitServer(ServerGroup group, int id, ServerVersion serverVersion) {
		super(group, id);
		this.serverVersion = serverVersion;
	}

	@Override
	public boolean isProxy() {
		return false;
	}

	public ServerVersion getServerVersion() {
		return serverVersion;
	}
}
