package de.terrarier.server.spigot;

import de.terrarier.lib.ServerVersion;
import de.terrarier.lib.SimpleUUID;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.multithreading.ProcessManager;
import de.terrarier.server.ServerInstance;

import java.util.concurrent.atomic.AtomicBoolean;

public final class BukkitServer extends ServerInstance<ServerGroup> {

	private boolean froze;
	private long lastPlayerQuit;
	private long lastWarmUp;
	private int freezeCountdown = 5;
	private final AtomicBoolean warmingUp = new AtomicBoolean();
	private final AtomicBoolean freezingIn = new AtomicBoolean();
	private final ServerVersion serverVersion;

	public BukkitServer(Process process, ServerGroup group, SimpleUUID uuid, int id, int port, ServerVersion serverVersion) {
		super(process, group, uuid, id, port);
		this.serverVersion = serverVersion;
	}

	public void freeze() {
		if (freezingIn.get()) {
			return;
		}
		if (warmingUp.get()) {
			return;
		}
		if (lastWarmUp + 10000L > System.currentTimeMillis()) {
			return;
		}
		freezingIn.set(true);
		try {
			if (group.getName().startsWith("Lobby") || group.getName().startsWith("Bungee")) {
				return;
			}
			if (froze) {
				return;
			}
			/*Logger.log("[" + LocalDateTime.now().toString().replaceAll("T", " | ") + "] Freezing "
					+ this.group.getName() + "-" + this.id + "...", LogType.INFO);*/
			final String serverName = group.getName() + "-" + id;
			Logger.log("Freezing " + serverName + "...", LogType.INFO);
			froze = true;
			ProcessManager.executeAndDestroyProcess(new ProcessBuilder("pkill", "-STOP", "-f", serverName));
			/*Logger.log("[" + LocalDateTime.now().toString().replaceAll("T", " | ") + "] Freezed " + this.group.getName()
					+ "-" + this.id, LogType.INFO);*/
			Logger.log("Froze " + serverName, LogType.INFO);
		} finally {
			freezingIn.set(false);
		}
	}

	public synchronized void warmUp() {
		if (warmingUp.get()) {
			return;
		}
		if (freezingIn.get()) {
			warmUp();
			return;
		}
		if(!froze) {
			return;
		}
		final long current = System.currentTimeMillis();
		if (lastWarmUp + 10000L > current) {
			return;
		}
		warmingUp.set(true);
		lastWarmUp = current;
		freezeCountdown = 5;
		try {
			if (!froze) {
				return;
			}
			/*Logger.log("[" + LocalDateTime.now().toString().replaceAll("T", " | ") + "] Warming up "
					+ this.group.getName() + "-" + this.id + "...", LogType.INFO);*/
			final String serverName = group.getName() + "-" + id;
			Logger.log("Warming up " + serverName + "...", LogType.INFO);
			ProcessManager.executeAndDestroyProcess(
					new ProcessBuilder("pkill", "-CONT", "-f" + serverName));
			/*Logger.log("[" + LocalDateTime.now().toString().replaceAll("T", " | ") + "] Warmed up "
					+ this.group.getName() + "-" + this.id, LogType.INFO);*/
			Logger.log("Warmed up " + serverName, LogType.INFO);
		} finally {
			froze = false;
			warmingUp.set(false);
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
		Logger.log("Der Server " + group.getName() + "-" + id + " ist gestartet!", LogType.INFO);
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
