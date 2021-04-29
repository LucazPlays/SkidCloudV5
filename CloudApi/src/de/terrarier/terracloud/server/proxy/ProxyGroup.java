package de.terrarier.terracloud.server.proxy;

import de.terrarier.terracloud.server.GroupInstance;

public final class ProxyGroup extends GroupInstance<ProxyServer> {
	
	public ProxyGroup(String name, int serverCount, boolean dynamic, int memory) {
		super(name, serverCount, dynamic, memory);
	}

	public ProxyServer addServer(int id) {
		final ProxyServer server = new ProxyServer(this, id);
		addServer(server);
		return server;
	}

}
