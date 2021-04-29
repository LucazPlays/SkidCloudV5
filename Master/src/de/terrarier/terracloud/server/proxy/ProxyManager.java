package de.terrarier.terracloud.server.proxy;

import com.mongodb.client.MongoCollection;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.database.MongoDataBase;
import de.terrarier.terracloud.file.FileUtil;
import de.terrarier.terracloud.multithreading.executables.proxy.StartProxy;
import de.terrarier.terracloud.multithreading.executables.proxy.StopProxy;
import de.terrarier.terracloud.server.InstanceManager;
import org.bson.Document;

import java.io.File;

public final class ProxyManager extends InstanceManager<ProxyServer, ProxyGroup> {

    @Override
    public void startServer(ProxyGroup group) {
        Master.getInstance().getExecutorService().executeAsync(new StartProxy(group));
    }

    public void startServer(ProxyGroup group, int id) {
        Master.getInstance().getExecutorService().executeAsync(new StartProxy(group, id));
    }

    @Override
    public void stopServer(ProxyServer server) {
        Master.getInstance().getExecutorService().executeAsync(new StopProxy(server));
    }

    public void addGroup(ProxyGroup group) {
        groups.put(group.getName(), group);
        for(int i = 1; i < group.getServerCount() + 1; i++) {
            Master.getInstance().getExecutorService().executeAsync(new StartProxy(group, i));
        }
    }

    @Override
    public void createGroup(ProxyGroup group) {
        new File("./Master/Templates/Proxy/" + group.getName() + "/").mkdir();
        Master.getInstance().getDataBase().getCollection("proxygroups").insertOne(
                new Document("name", group.getName())
                        .append("servers", group.getServerCount())
                        .append("memory", group.getMemory())
                        .append("dynamic", group.isDynamic()));
        addGroup(group);
    }

    @Override
    public void initGroups() {
        final MongoCollection<Document> collection = Master.getInstance().getDataBase().getCollection("proxygroups");
        for (Document next : collection.find()) {
            addGroup(new ProxyGroup(MongoDataBase.get(next, "name"), MongoDataBase.get(next, "servers"),
                    MongoDataBase.get(next, "dynamic"), MongoDataBase.get(next, "memory")));
        }
    }

    @Override
    public void deleteGroup(String name) {
        final ProxyGroup group = groups.remove(name);
        if(group == null) {
            return;
        }
        group.delete();
        final MongoCollection<Document> collection = Master.getInstance().getDataBase().getCollection("proxygroups");
        collection.deleteOne(new Document("name", name));
        final File file = new File("./Master/Templates/Proxy/" + group.getName() + "/");
        FileUtil.deleteDir(file);
    }

}
