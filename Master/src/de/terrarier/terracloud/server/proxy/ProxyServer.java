package de.terrarier.terracloud.server.proxy;

import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.server.wrapper.Wrapper;

public final class ProxyServer extends ServerInstance<ProxyGroup> {

    public ProxyServer(ProxyGroup group, int id, Wrapper wrapper) {
       super(group, id, wrapper);
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
