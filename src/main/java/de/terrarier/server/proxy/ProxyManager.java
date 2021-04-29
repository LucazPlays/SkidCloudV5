package de.terrarier.server.proxy;

import de.terrarier.Wrapper;
import de.terrarier.file.FileManager;
import de.terrarier.multithreading.executables.proxy.StartProxy;
import de.terrarier.multithreading.executables.proxy.StopProxy;
import de.terrarier.netlistening.Connection;
import de.terrarier.server.InstanceManager;
import de.terrarier.terracloud.packet.Packet;
import de.terrarier.terracloud.packet.PacketDirection;
import de.terrarier.terracloud.packet.PacketSource;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ProxyManager extends InstanceManager<ProxyServer, ProxyGroup> {

    private static final String DYNAMIC_PATH = "/Wrapper/Temp/Proxys/";
    private static final String STATIC_PATH = "/Wrapper/Proxys/";

    public void startServer(ProxyGroup group) {
        Wrapper.getInstance().getExecutorService().executeAsync(new StartProxy(group));
    }

    public void stopServer(ProxyServer server) {
        Wrapper.getInstance().getExecutorService().executeAsync(new StopProxy(server));
    }

    public void addGroup(ProxyGroup group) {
        this.groups.put(group.getName(), group);
        for(int i = 1; i < group.getServerCount() + 1; i++) {
            Wrapper.getInstance().getExecutorService().executeAsync(new StartProxy(group, i));
           /* try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    public void createGroup(ProxyGroup group) {
        new File("./Wrapper/Templates/Proxy/" + group.getName() + "/").mkdir();
        Wrapper.getInstance().getSql().write("INSERT INTO terracloud__proxygroups (name, servers, memory, dynamic) VALUES ('" + group.getName() + "', " + group.getServerCount() + ", " + group.getMemory() + ", " + (group.isDynamic() ? 1 : 0) + ")");
        addGroup(group);
    }

    public void initGroups() {
        final PreparedStatement ps = Wrapper.getInstance().getSql().read("SELECT * FROM terracloud__proxygroups");
        try {
            final ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                addGroup(new ProxyGroup(rs.getString("name"), rs.getInt("servers"), rs.getBoolean("dynamic"), rs.getInt("memory")));
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

    public void sendPacket(Packet packet, PacketSource source) {
        for(ProxyGroup serverGroup : groups.values()) {
            for(ProxyServer proxyServer : serverGroup.getServers()) {
                if(proxyServer.isStarted())
                    packet._write(proxyServer.getConnection(), PacketDirection.PROXY, source);
            }
        }
    }

    public ProxyServer getServer(Connection connection) {
        for(ProxyGroup group : groups.values()) {
            for(ProxyServer proxyServer : group.getServers()) {
                if(proxyServer.isStarted() && proxyServer.getConnection().equals(connection))
                    return proxyServer;
            }
        }
        return null;
    }

    public String getPath(boolean dynamic, String name, int id) {
        final String typePath = dynamic ? DYNAMIC_PATH : STATIC_PATH;
        return FileManager.DEFAULT_PATH + typePath + name + "/" + name + "-" + id;
    }

}
