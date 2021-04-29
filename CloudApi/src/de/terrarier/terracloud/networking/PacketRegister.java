package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.CloudApi;
import de.terrarier.terracloud.event.InstanceStartedEvent;
import de.terrarier.terracloud.event.InstanceStartingEvent;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyManager;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.ServerGroup;
import de.terrarier.terracloud.server.spigot.ServerManager;
import org.bukkit.Bukkit;

public class PacketRegister extends Packet {

    public PacketRegister() {}

    @Override
    protected void write0(DataContainer dataContainer) {
        dataContainer.addAll(ServiceType.MINECRAFT.id(), CloudApi.SERVER_GROUP, CloudApi.SERVER_ID);
    }

    @Override
    protected void read0(DataContainer dataContainer) {
        final byte serviceTypeId = dataContainer.read();
        final ServiceType serviceType = ServiceType.SERVICE_TYPES[serviceTypeId];
        switch (serviceType) {
            case MINECRAFT:
                String groupName = dataContainer.read();
                int id = dataContainer.read();
                String host = dataContainer.read();
                final ServerManager serverManager = CloudApi.getInstance().getServerManager();
                final ServerGroup serverGroup = serverManager.getGroup(groupName);
                if(serverGroup != null) {
                    BukkitServer server = serverGroup.getServer(id);
                    if (server == null) {
                        server = serverGroup.addServer(id);
                        Bukkit.getPluginManager().callEvent(new InstanceStartingEvent(server));
                    }
                    server.started(host);
                    Bukkit.getPluginManager().callEvent(new InstanceStartedEvent(server));
                }
                break;
            case PROXY:
                groupName = dataContainer.read();
                id = dataContainer.read();
                host = dataContainer.read();
                final ProxyManager proxyManager = CloudApi.getInstance().getProxyManager();
                final ProxyGroup proxyGroup = proxyManager.getGroup(groupName);
                if(proxyGroup != null) {
                    ProxyServer server = proxyGroup.getServer(id);
                    if (server == null) {
                        server = proxyGroup.addServer(id);
                        Bukkit.getPluginManager().callEvent(new InstanceStartingEvent(server));
                    }
                    server.started(host);
                    Bukkit.getPluginManager().callEvent(new InstanceStartedEvent(server));
                }
                break;
        }
    }

    @Override
    public int getId() {
        return 0x0;
    }
}
