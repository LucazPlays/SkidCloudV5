package de.terrarier.terracloud.server.spigot;

import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.multithreading.ProcessManager;
import de.terrarier.terracloud.server.LocalServerInstance;

public final class LocalBukkitServerImpl extends LocalServerInstance<ServerGroup> implements BukkitServer {

	private boolean froze;
	private long lastPlayerQuit;
	private long lastWarmUp;
	private int freezeCountdown = 5;
	private final ServerVersion serverVersion;

	public LocalBukkitServerImpl(Process process, ServerGroup group, int id, int port, ServerVersion serverVersion) {
		super(group, id, process, port);
		this.serverVersion = serverVersion;
	}

	public void freeze() {
		synchronized (this) {
			if (lastWarmUp + 10000L > System.currentTimeMillis()) {
				return;
			}
			if (group.getName().equals("Lobby")) {
				return;
			}
			if (froze) {
				return;
			}
			final String serverName = group.getName() + "-" + id;
			Logger.log("Der Server " + serverName + " wird eingeskisddet...", LogType.INFO);
			froze = true;
			ProcessManager.executeAndDestroyProcess(new ProcessBuilder("pkill", "-STOP", "-f", serverName));
			Logger.log("Der Server " + serverName + " wurde eingeskiddet.", LogType.INFO);
		}
	}

	public synchronized void warmUp() {
		synchronized (this) {
			if (!froze) {
				return;
			}
			final long current = System.currentTimeMillis();
			if (lastWarmUp + 10000L > current) {
				return;
			}
			lastWarmUp = current;
			freezeCountdown = 5;
			try {
				if (!froze) {
					return;
				}
				final String serverName = group.getName() + "-" + id;
				Logger.log("Der Server " + serverName + " wird aufgeskiddet...", LogType.INFO);
				ProcessManager.executeAndDestroyProcess(
						new ProcessBuilder("pkill", "-CONT", "-f" + serverName));
				Logger.log("Der Server " + serverName + " wurde aufgeskiddet.", LogType.INFO);
			} finally {
				froze = false;
			}
		}
	}
	
	public boolean doesFreeze() {
		return --freezeCountdown == 0;
	}
	
	public void resetFreezeCount() {
		freezeCountdown = 5;
	}

	@Override
	public void playerQuit() {
		if (--players == 0)
			lastPlayerQuit = System.currentTimeMillis();
	}

	@Override
	public void logStarted() {
		Logger.log("Der Server " + group.getName() + "-" + id + " ist geskiddet!", LogType.INFO);
	}

	@Override
	public boolean isProxy() {
		return false;
	}

	public long lastQuit() {
		return lastPlayerQuit;
	}

	public boolean isFroze() {
		return froze;
	}

	public boolean hasWarmUpBenefit() {
		return lastWarmUp + 31000L > System.currentTimeMillis();
	}

	public ServerVersion getServerVersion() {
		return serverVersion;
	}
}
