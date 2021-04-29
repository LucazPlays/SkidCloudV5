package de.terrarier.terracloud.server.proxy;

import de.terrarier.terracloud.server.InstanceManager;

public final class ProxyManager extends InstanceManager<ProxyServer, ProxyGroup> {

    public void addGroup(ProxyGroup group) {
        groups.put(group.getName(), group);
    }

}
