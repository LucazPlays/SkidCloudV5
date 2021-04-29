package de.terrarier.terracloud.server;

public abstract class ServerInstance<T extends GroupInstance<?>> {

    protected final T group;
    protected final int id;
    private String host;
    protected int players;
    private long started;
    private int maxPlayers;

    public ServerInstance(T group, int id) {
        this.group = group;
        this.id = id;
    }

    public void updatePlayerCount(int players, int maxPlayers) {
        this.players = players;
        this.maxPlayers = maxPlayers;
    }

    public void started(String host) {
        this.host = host;
        started = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public T getGroup() {
        return group;
    }

    public int getPlayers() {
        return players;
    }

    public long getStart() {
        return started;
    }

    public boolean isStarted() {
        return started != 0L;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public abstract boolean isProxy();

    public String getHost() {
        return host;
    }
}
