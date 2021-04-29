package de.terrarier.server.spigot;

import de.terrarier.Wrapper;
import de.terrarier.file.FileManager;
import de.terrarier.lib.ServerVersion;
import de.terrarier.multithreading.executables.spigot.StartServer;
import de.terrarier.multithreading.executables.spigot.StopServer;
import de.terrarier.netlistening.Connection;
import de.terrarier.server.InstanceManager;
import de.terrarier.terracloud.packet.Packet;
import de.terrarier.terracloud.packet.PacketDirection;
import de.terrarier.terracloud.packet.PacketSource;
import de.terrarier.utils.CallbackReference;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	public void stopServer(BukkitServer server) {
		Wrapper.getInstance().getExecutorService().executeAsync(new StopServer(server));
	}
	
	private void addGroup(ServerGroup group) {
		this.groups.put(group.getName(), group);
		for(int i = 1; i < group.getServerCount() + 1; i++) {
			Wrapper.getInstance().getExecutorService().executeAsync(new StartServer(group, i));
			/*try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
	}

	public void createGroup(ServerGroup group) {
		new File("./Wrapper/Templates/Server/" + group.getName() + "/").mkdir();
		Wrapper.getInstance().getSql().write("INSERT INTO `terracloud__servergroups` (name, servers, memory, dynamic, version) VALUES ('" + group.getName() + "', " + group.getServerCount() + ", " + group.getMemory() + ", " + (group.isDynamic() ? 1 : 0) + ", '" + group.getDefaultServerVersion().name() + "')");
		addGroup(group);
	}

	public void initGroups() {
		final PreparedStatement ps = Wrapper.getInstance().getSql().read("SELECT * FROM terracloud__servergroups");
		try {
			final ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				addGroup(new ServerGroup(rs.getString("name"), rs.getInt("servers"), rs.getBoolean("dynamic"), rs.getInt("memory"), ServerVersion.valueOf(rs.getString("version"))));
			}
		} catch (SQLException throwable) {
			throwable.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException throwable) {
				throwable.printStackTrace();
			}
		}
	}

	public String getPath(boolean dynamic, String name, int id) {
		final String typePath = dynamic ? DYNAMIC_PATH : STATIC_PATH;
		return FileManager.DEFAULT_PATH + typePath + name + "/" + name + "-" + id;
	}

	public void sendPacket(Packet packet, PacketSource source) {
		for(ServerGroup serverGroup : groups.values()) {
			for(BukkitServer server : serverGroup.getServers()) {
				if(server.isStarted())
					packet._write(server.getConnection(), PacketDirection.SERVER, source);
			}
		}
	}

	public BukkitServer getServer(Connection connection) {
		for(ServerGroup group : groups.values()) {
			for(BukkitServer server : group.getServers()) {
				if(server.isStarted() && server.getConnection().equals(connection))
					return server;
			}
		}
		return null;
	}

}
