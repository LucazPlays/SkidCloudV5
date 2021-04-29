package de.terrarier.terracloud.server.proxy;

import de.terrarier.netlistening.Connection;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.file.FileUtil;
import de.terrarier.terracloud.multithreading.executables.proxy.StartProxy;
import de.terrarier.terracloud.multithreading.executables.proxy.StopProxy;
import de.terrarier.terracloud.networking.PacketUpdateGroup;
import de.terrarier.terracloud.server.InstanceManager;

import java.io.File;

public final class ProxyManager extends InstanceManager<ProxyServer, ProxyGroup> {

    private static final String DYNAMIC_PATH = "/Wrapper/Temp/Proxys/";
    private static final String STATIC_PATH = "/Wrapper/Proxys/";

    public void startServer(ProxyGroup group) {
        Wrapper.getInstance().getExecutorService().executeAsync(new StartProxy(group));
    }

    public void startServer(ProxyGroup group, int id) {
        Wrapper.getInstance().getExecutorService().executeAsync(new StartProxy(group, id));
    }

    public void stopServer(ProxyServer server, boolean preventRestart) {
        Wrapper.getInstance().getExecutorService().executeAsync(new StopProxy(server, preventRestart));
    }

    public void addGroup(ProxyGroup group) {
        groups.put(group.getName(), group);
        final File templateFolder = new File("./Wrapper/Templates/Proxy/" + group.getName() + "/");
        if(!templateFolder.exists()) {
            templateFolder.mkdir();
        }
        Wrapper.getInstance().sendToMaster(new PacketUpdateGroup(group, true));
    }

    public void createGroup(ProxyGroup group) {
        new File("./Wrapper/Templates/Proxy/" + group.getName() + "/").mkdir();
        addGroup(group);
    }

    public ProxyServer getServer(Connection connection) {
        for(ProxyGroup group : groups.values()) {
            for(ProxyServer proxyServer : group.getServers()) {
                if(proxyServer.isStarted() && proxyServer instanceof LocalProxyServerImpl &&
                        ((LocalProxyServerImpl) proxyServer).getConnection().equals(connection))
                    return proxyServer;
            }
        }
        return null;
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
        final ProxyGroup group = groups.remove(groupName);
        if (group == null) {
            return false;
        }
        for (ProxyServer server : group.getServers()) {
            stopServer(server, true);
        }
        Wrapper.getInstance().broadcastPacket(new PacketUpdateGroup(groupName, false));
        final File templateFolder = new File("./Wrapper/Templates/Proxy/" + groupName + "/");
        FileUtil.deleteDir(templateFolder);
        return true;
    }

}
