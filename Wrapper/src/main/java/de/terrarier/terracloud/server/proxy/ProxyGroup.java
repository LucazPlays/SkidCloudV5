package de.terrarier.terracloud.server.proxy;

import de.terrarier.terracloud.server.GroupInstance;

public final class ProxyGroup extends GroupInstance<ProxyServer> {
	
	public ProxyGroup(String name, int serverCount, boolean dynamic, int memory) {
		super(name, serverCount, dynamic, memory);
	}

	public LocalProxyServerImpl addLocalServer(int id, Process process, int port) {
		final LocalProxyServerImpl server = new LocalProxyServerImpl(this, id, process, port);
		addServer(server);
		return server;
	}

	public ProxyServer addServer(int id) {
		final ProxyServer server = new ProxyServerImpl(this, id);
		addServer(server);
		return server;
	}

}
