package de.terrarier.terracloud.server;

import de.terrarier.netlistening.Connection;

public abstract class ServerInstanceImpl<T extends GroupInstance<?>> implements ServerInstance<T> {

    protected final T group;
    protected final int id;
    protected int players;
    protected long started;
    protected int maxPlayers;

    public ServerInstanceImpl(T group, int id) {
        this.group = group;
        this.id = id;
    }

    public void playerQuit() {
        this.players--;
    }

    public void playerJoined(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.players++;
    }

    public void started(Connection connection) {
        this.started = System.currentTimeMillis();
        logStarted();
    }

    public abstract void logStarted();

    public int getId() {
        return this.id;
    }

    public T getGroup() {
        return this.group;
    }

    public int getPlayers() {
        return this.players;
    }

    public long getStart() {
        return this.started;
    }

    public boolean isStarted() {
        return started != 0L;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public abstract boolean isProxy();

}
