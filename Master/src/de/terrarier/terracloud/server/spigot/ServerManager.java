package de.terrarier.terracloud.server.spigot;

import com.mongodb.client.MongoCollection;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.database.MongoDataBase;
import de.terrarier.terracloud.file.FileUtil;
import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.multithreading.executables.spigot.StartServer;
import de.terrarier.terracloud.multithreading.executables.spigot.StopServer;
import de.terrarier.terracloud.networking.PacketUpdateGroup;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.utils.CallbackReference;
import org.bson.Document;

import java.io.File;

public final class ServerManager extends InstanceManager<BukkitServer, ServerGroup> {

	@Override
	public void startServer(ServerGroup group) {
		Master.getInstance().getExecutorService().executeAsync(new StartServer(group));
	}

	public void startServer(ServerGroup group, CallbackReference callbackReference) {
		Master.getInstance().getExecutorService().executeAsync(new StartServer(group, callbackReference));
	}

	public void startServer(ServerGroup group, int id) {
		Master.getInstance().getExecutorService().executeAsync(new StartServer(group, id));
	}

	public void startServer(ServerGroup group, int id, ServerVersion serverVersion) {
		Master.getInstance().getExecutorService().executeAsync(new StartServer(group, id, serverVersion));
	}

	@Override
	public void stopServer(BukkitServer server) {
		Master.getInstance().getExecutorService().executeAsync(new StopServer(server));
	}

	private void addGroup(ServerGroup group) {
		this.groups.put(group.getName(), group);
		for(int i = 1; i < group.getServerCount() + 1; i++) {
			Master.getInstance().getExecutorService().executeAsync(new StartServer(group, i));
		}
	}

	@Override
	public void createGroup(ServerGroup group) {
		new File("./Master/Templates/Server/" + group.getName() + "/").mkdir();
		Master.getInstance().getDataBase().getCollection("servergroups").insertOne(
				new Document("name", group.getName())
						.append("servers", group.getServerCount())
						.append("memory", group.getMemory())
						.append("dynamic", group.isDynamic())
						.append("version", group.getDefaultServerVersion().name()));
		Master.getInstance().broadcastPacket(new PacketUpdateGroup(group, false));
		addGroup(group);
	}

	@Override
	public void initGroups() {
		for (Document next : Master.getInstance().getDataBase().getCollection("servergroups").find()) {
			addGroup(new ServerGroup(MongoDataBase.get(next, "name"), MongoDataBase.get(next, "servers"),
					MongoDataBase.get(next, "dynamic"), MongoDataBase.get(next, "memory"),
					ServerVersion.valueOf(MongoDataBase.get(next, "version"))));
		}
	}

	@Override
	public void deleteGroup(String name) {
		final ServerGroup group = groups.remove(name);
		if (group == null) {
			return;
		}
		group.delete();
		final MongoCollection<Document> collection = Master.getInstance().getDataBase().getCollection("servergroups");
		collection.deleteOne(new Document("name", name));
		final File file = new File("./Master/Templates/Server/" + group.getName() + "/");
		FileUtil.deleteDir(file);
	}

}
