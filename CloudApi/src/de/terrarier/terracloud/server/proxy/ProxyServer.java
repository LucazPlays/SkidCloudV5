package de.terrarier.terracloud.server.proxy;

import de.terrarier.terracloud.server.ServerInstance;

public final class ProxyServer extends ServerInstance<ProxyGroup> {

    public ProxyServer(ProxyGroup group, int id) {
       super(group, id);
    }

    @Override
    public boolean isProxy() {
        return true;
    }

}
