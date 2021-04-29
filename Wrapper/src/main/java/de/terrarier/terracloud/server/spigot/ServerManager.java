package de.terrarier.terracloud.server.spigot;

import de.terrarier.netlistening.Connection;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.file.FileUtil;
import de.terrarier.terracloud.lib.CustomPayloadUtil;
import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.multithreading.executables.spigot.StartServer;
import de.terrarier.terracloud.multithreading.executables.spigot.StopServer;
import de.terrarier.terracloud.networking.PacketUpdateGroup;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.utils.CallbackReference;

import java.io.File;

public final class ServerManager extends InstanceManager<BukkitServer, ServerGroup> {

	private static final String DYNAMIC_PATH = "/Wrapper/Temp/Servers/";
	private static final String STATIC_PATH = "/Wrapper/Servers/";

	public void startServer(ServerGroup group) {
		Wrapper.getInstance().getExecutorService().executeAsync(new StartServer(group));
	}

	public void startServer(ServerGroup group, CallbackReference callbackReference) {
		Wrapper.getInstance().getExecutorService().executeAsync(new StartServer(group, callbackReference));
	}

	public void startServer(ServerGroup group, int id) {
		Wrapper.getInstance().getExecutorService().executeAsync(new StartServer(group, id));
	}

	public void startServer(ServerGroup group, int id, ServerVersion serverVersion) {
		Wrapper.getInstance().getExecutorService().executeAsync(new StartServer(group, id, serverVersion));
	}

	@Override
	public void stopServer(BukkitServer server, boolean preventRestart) {
		Wrapper.getInstance().getExecutorService().executeAsync(new StopServer(server, preventRestart));
	}
	
	private void addGroup(ServerGroup group) {
		groups.put(group.getName(), group);
		final File templateFolder = new File("./Wrapper/Templates/Server/" + group.getName() + "/");
		if(!templateFolder.exists()) {
			templateFolder.mkdir();
		}
	}

	public void createGroup(ServerGroup group) {
		new File("./Wrapper/Templates/Server/" + group.getName() + "/").mkdir();
		addGroup(group);
		Wrapper.getInstance().sendToMaster(new PacketUpdateGroup(group, false));
	}

	public String getGroupPath(boolean dynamic, String name) {
		final String typePath = dynamic ? DYNAMIC_PATH : STATIC_PATH;
		return FileUtil.DEFAULT_PATH + typePath + name;
	}

	public String getPath(boolean dynamic, String name, int id) {
		return getGroupPath(dynamic, name) + "/" + name + "-" + id;
	}

	@Override
	public boolean deleteGroup(String groupName) {
		final ServerGroup group = groups.remove(groupName);
		if (group == null) {
			return false;
		}
		for (BukkitServer server : group.getServers()) {
			stopServer(server, true);
		}
		Wrapper.getInstance().broadcastPacket(new PacketUpdateGroup(groupName, false));
		final File templateFolder = new File("./Wrapper/Templates/Server/" + groupName + "/");
		FileUtil.deleteDir(templateFolder);
		return true;
	}

	public BukkitServer getServer(Connection connection) {
		for(ServerGroup group : groups.values()) {
			for(BukkitServer server : group.getServers()) {
				if(server.isStarted() && server instanceof LocalServerInstance && ((LocalServerInstance<?>) server)
						.getConnection().equals(connection))
					return server;
			}
		}
		return null;
	}

}
