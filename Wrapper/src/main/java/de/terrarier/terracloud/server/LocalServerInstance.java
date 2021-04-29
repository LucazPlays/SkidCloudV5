package de.terrarier.terracloud.server;

import de.terrarier.netlistening.Connection;
import de.terrarier.terracloud.utils.OSType;
import de.terrarier.terracloud.utils.ProcessUtil;

import java.io.IOException;

public abstract class LocalServerInstance<T extends GroupInstance<?>> extends ServerInstanceImpl<T> {

    protected int players;
    protected long started;
    protected final Process process;
    protected final int port;
    protected Connection connection;
    protected int maxPlayers;

    public LocalServerInstance(T group, int id, Process process, int port) {
        super(group, id);
        this.process = process;
        this.port = port;
    }

    public void playerQuit() {
        this.players--;
    }

    public void playerJoined(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.players++;
    }

    public final void started(Connection connection) {
        this.started = System.currentTimeMillis();
        this.connection = connection;
        logStarted();
        // TODO: Actually implement this with an own native process lib!
        /*if(OSType.getOSType() == OSType.LINUX) {
            try {
                System.out.println("renicing...");
                Runtime.getRuntime().exec("sudo renice -n 0 -p " + ProcessUtil.getProcessId(process));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
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

    public Process getProcess() {
        return this.process;
    }

    public int getPort() {
        return this.port;
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

}
