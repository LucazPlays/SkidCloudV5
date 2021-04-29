package de.terrarier.server.proxy;

import de.terrarier.lib.SimpleUUID;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.server.ServerInstance;

public final class ProxyServer extends ServerInstance<ProxyGroup> {

    public ProxyServer(Process process, ProxyGroup group, SimpleUUID uuid, int id, int port) {
       super(process, group, uuid, id, port);
    }

    @Override
    public void logStarted() {
        Logger.log("Der ProxyServer " + group.getName() + "-" + id + " ist gestartet!", LogType.INFO);
    }

    @Override
    public boolean isProxy() {
        return true;
    }

}
