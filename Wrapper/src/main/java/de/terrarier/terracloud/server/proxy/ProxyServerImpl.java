package de.terrarier.terracloud.server.proxy;

import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.ServerInstanceImpl;

public class ProxyServerImpl extends ServerInstanceImpl<ProxyGroup> implements ProxyServer {

    public ProxyServerImpl(ProxyGroup group, int id) {
       super(group, id);
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
