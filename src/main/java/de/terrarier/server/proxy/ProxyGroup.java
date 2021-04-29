package de.terrarier.server.proxy;

import de.terrarier.lib.SimpleUUID;
import de.terrarier.server.GroupInstance;

public final class ProxyGroup extends GroupInstance<ProxyServer> {
	
	public ProxyGroup(String name, int serverCount, boolean dynamic, int memory) {
		super(name, serverCount, dynamic, memory);
	}

	public ProxyServer addServer(Process process, SimpleUUID uuid, int id, int port) {
		final ProxyServer server = new ProxyServer(process, this, uuid, id, port);
		addServer(server);
		return server;
	}

}
