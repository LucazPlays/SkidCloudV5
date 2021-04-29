package de.terrarier.terracloud.server;

import de.terrarier.terracloud.server.wrapper.Wrapper;

public abstract class ServerInstance<T extends GroupInstance<?>> {

    protected final T group;
    protected final int id;
    private final Wrapper wrapper;
    protected int players;
    private long started;
    private int maxPlayers;

    public ServerInstance(T group, int id, Wrapper wrapper) {
        this.group = group;
        this.id = id;
        this.wrapper = wrapper;
    }

    public void playerQuit() {
        this.players--;
    }

    public void playerJoined(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.players++;
    }

    public void started() {
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
        return this.started != 0L;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public abstract boolean isProxy();

    public Wrapper getWrapper() {
        return wrapper;
    }
}
