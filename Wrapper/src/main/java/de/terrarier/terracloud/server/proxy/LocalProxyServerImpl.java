package de.terrarier.terracloud.server.proxy;

import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.LocalServerInstance;

public class LocalProxyServerImpl extends LocalServerInstance<ProxyGroup> implements ProxyServer {

    public LocalProxyServerImpl(ProxyGroup group, int id, Process process, int port) {
        super(group, id, process, port);
    }

    @Override
    public void logStarted() {
        Logger.log("Der ProxyServer " + getGroup().getName() + "-" + getId() + " ist gestartet!", LogType.INFO);
    }

    @Override
    public boolean isProxy() {
        return true;
    }

}
