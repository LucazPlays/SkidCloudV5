package de.terrarier.server;

import de.terrarier.lib.SimpleUUID;
import de.terrarier.netlistening.Connection;

public abstract class ServerInstance<T extends GroupInstance<?>> {

    protected final int id;
    protected final T group;
    protected int players;
    private long started;
    private final SimpleUUID uuid;
    private final Process process;
    private Connection connection;
    private int maxPlayers;
    // private PublicKey publicKey;
    private final int port;

    public ServerInstance(Process process, T group, SimpleUUID uuid, int id, int port) {
        this.process = process;
        this.group = group;
        this.uuid = uuid;
        this.id = id;
        this.port = port;
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
        this.connection = connection;
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

    public SimpleUUID getUUID() {
        return this.uuid;
    }

    public Process getProcess() {
        return this.process;
    }

    public boolean isStarted() {
        return this.started != 0L;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /*public PublicKey getKey() {
        return this.publicKey;
    }

    public void setKey(PublicKey key) {
        this.publicKey = key;
    }*/

    public int getPort() {
        return this.port;
    }

    public abstract boolean isProxy();

}
