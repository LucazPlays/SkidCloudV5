package de.terrarier.terracloud.server.spigot;

import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.ServerInstanceImpl;

public class BukkitServerImpl extends ServerInstanceImpl<ServerGroup> implements BukkitServer {

    private int port;
    private String host;

    public BukkitServerImpl(ServerGroup group, int id) {
        super(group, id);
    }

    @Override
    public void logStarted() {
        Logger.log("Der Server " + getGroup().getName() + "-" + getId() + " ist gestartet!", LogType.INFO);
    }

    @Override
    public boolean isProxy() {
        return false;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setConnectionProps(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public String getHost() {
        return host;
    }

}
