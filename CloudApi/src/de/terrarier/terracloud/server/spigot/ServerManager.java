package de.terrarier.terracloud.server.spigot;

import de.terrarier.terracloud.server.InstanceManager;

public final class ServerManager extends InstanceManager<BukkitServer, ServerGroup> {
	
	private void addGroup(ServerGroup group) {
		groups.put(group.getName(), group);
	}

}
