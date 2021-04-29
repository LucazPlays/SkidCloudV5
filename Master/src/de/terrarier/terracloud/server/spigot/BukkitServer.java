package de.terrarier.terracloud.server.spigot;

import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.server.wrapper.Wrapper;

public final class BukkitServer extends ServerInstance<ServerGroup> {

	private long lastPlayerQuit;
	private final ServerVersion serverVersion;
	private int port;

	public BukkitServer(ServerGroup group, int id, Wrapper wrapper, ServerVersion serverVersion) {
		super(group, id, wrapper);
		this.serverVersion = serverVersion;
	}

	@Override
	public void playerQuit() {
		if (--players == 0)
			lastPlayerQuit = System.currentTimeMillis();
	}

	@Override
	public void logStarted() {
		Logger.log("Der Server " + group.getName() + "-" + id + " ist gestartet!", LogType.INFO);
	}

	@Override
	public boolean isProxy() {
		return false;
	}

	public long lastQuit() {
		return lastPlayerQuit;
	}

	public ServerVersion getServerVersion() {
		return serverVersion;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}
}
