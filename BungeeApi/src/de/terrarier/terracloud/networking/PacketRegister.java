package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.BungeeCloudApi;
import de.terrarier.terracloud.event.ConnectedEvent;
import de.terrarier.terracloud.event.InstanceStartedEvent;
import de.terrarier.terracloud.event.InstanceStartingEvent;
import de.terrarier.terracloud.server.proxy.BungeeSetting;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyManager;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.ServerGroup;
import de.terrarier.terracloud.server.spigot.ServerManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class PacketRegister extends Packet {

    public PacketRegister() {}

    @Override
    protected void write0(DataContainer dataContainer) {
        dataContainer.addAll(ServiceType.PROXY.id(), BungeeCloudApi.SERVER_GROUP, BungeeCloudApi.SERVER_ID);
    }

    @Override
    protected void read0(DataContainer dataContainer) {
        final byte serviceTypeId = dataContainer.read();
        final ServiceType serviceType = ServiceType.SERVICE_TYPES[serviceTypeId];
        switch (serviceType) {
            case WRAPPER:
                final BungeeSetting bungeeSetting = new BungeeSetting(dataContainer.read(), dataContainer.read(),
                        dataContainer.read(), dataContainer.read());
                BungeeCloudApi.getInstance().init(bungeeSetting);
                BungeeCord.getInstance().getPluginManager().callEvent(new ConnectedEvent());
                break;
            case MINECRAFT:
                String groupName = dataContainer.read();
                int id = dataContainer.read();
                final int port = dataContainer.read();
                String host = dataContainer.read();
                final ServerManager serverManager = BungeeCloudApi.getInstance().getServerManager();
                final ServerGroup serverGroup = serverManager.getGroup(groupName);
                if (serverGroup != null) {
                    BukkitServer server = serverGroup.getServer(id);
                    if (server == null) {
                        server = serverGroup.addServer(id);
                        BungeeCord.getInstance().getPluginManager().callEvent(new InstanceStartingEvent(server));
                    }
                    final String comb = groupName + "-" + id;
                    final ServerInfo info = BungeeCord.getInstance().constructServerInfo(comb,
                            new InetSocketAddress(host, port), "", false);
                    BungeeCord.getInstance().getServers().put(comb, info);
                    server.started(host);
                    BungeeCord.getInstance().getPluginManager().callEvent(new InstanceStartedEvent(server));
                }
                break;
            case PROXY:
                groupName = dataContainer.read();
                id = dataContainer.read();
                host = dataContainer.read();
                final ProxyManager proxyManager = BungeeCloudApi.getInstance().getProxyManager();
                final ProxyGroup proxyGroup = proxyManager.getGroup(groupName);
                if (proxyGroup != null) {
                    ProxyServer server = proxyGroup.getServer(id);
                    if (server == null) {
                        server = proxyGroup.addServer(id);
                        BungeeCord.getInstance().getPluginManager().callEvent(new InstanceStartingEvent(server));
                    }
                    server.started(host);
                    BungeeCord.getInstance().getPluginManager().callEvent(new InstanceStartedEvent(server));
                }
                break;
        }
    }

    @Override
    public int getId() {
        return 0x0;
    }
}
