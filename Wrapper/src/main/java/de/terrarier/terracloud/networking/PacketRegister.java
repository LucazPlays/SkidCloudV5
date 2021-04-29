package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.server.proxy.BungeeSetting;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.BukkitServerImpl;
import de.terrarier.terracloud.server.spigot.LocalBukkitServerImpl;
import de.terrarier.terracloud.server.spigot.ServerGroup;
import de.terrarier.terracloud.utils.SystemUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketRegister extends Packet {

    private String serviceName;
    private ServiceType serviceType;
    private int id;
    private int port;
    private String host;

    public PacketRegister(String serviceName, ServiceType serviceType) {
        this.serviceName = serviceName;
        this.serviceType = serviceType;
    }

    public PacketRegister(String serviceName, ServiceType serviceType, int id) {
        this(serviceName, serviceType);
        this.id = id;
    }

    public PacketRegister(String serviceName, ServiceType serviceType, int id, String host, int port) {
        this(serviceName, serviceType, id);
        this.host = host;
        this.port = port;
    }

    public PacketRegister() {}

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.add(serviceType.id());
        if(serviceType == ServiceType.WRAPPER) {
            dataContainer.setEncrypted(true);
            dataContainer.addAll(Wrapper.getInstance().getSetting().getKey(), serviceName,
                    SystemUtil.getAvailableProcessors(), SystemUtil.systemMemory());
        }else {
            dataContainer.addAll(serviceName, id);
            if(serviceType == ServiceType.MINECRAFT && direction != ServiceType.MINECRAFT) {
                dataContainer.add(port);
            }
            try {
                dataContainer.add(host == null ? InetAddress.getLocalHost().getHostAddress() : host);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        final byte serviceId = dataContainer.read();
        serviceType = ServiceType.SERVICE_TYPES[serviceId];
        switch (serviceType) {
            case MINECRAFT:
                serviceName = dataContainer.read();
                id = dataContainer.read();
                final ServerGroup group = Wrapper.getInstance().getServerManager().getGroup(serviceName);
                if(group != null) {
                    final BukkitServer server = group.getServer(id);
                    if (server != null) {
                        if (server instanceof LocalServerInstance<?>) {
                            port = ((LocalBukkitServerImpl) server).getPort();
                            Wrapper.getInstance().sendToMaster(this);
                            host = "localhost";
                            sendServerRegister(connection, serviceType);
                        } else {
                            port = dataContainer.read();
                            ((BukkitServerImpl) server).setConnectionProps(port, host);
                        }
                        server.started(connection);
                    } else {
                        port = dataContainer.read();
                        final BukkitServerImpl bukkitServer = (BukkitServerImpl) group.addServer(id);
                        bukkitServer.started(null);
                        bukkitServer.setConnectionProps(port, host);
                        host = dataContainer.read();
                    }
                }
                Wrapper.getInstance().broadcastPacket(this);
                break;
            case PROXY:
                serviceName = dataContainer.read();
                id = dataContainer.read();
                final ProxyGroup proxyGroup = Wrapper.getInstance().getProxyManager().getGroup(serviceName);
                if(proxyGroup != null) {
                    final ProxyServer proxyServer = proxyGroup.getServer(id);
                    if (proxyServer != null) {
                        if (proxyServer instanceof LocalServerInstance<?>) {
                            final DataContainer packetData = new DataContainer();
                            final BungeeSetting bungeeSetting = Wrapper.getInstance().getSetting().getBungeeSetting();
                            packetData.addAll(getId(), ServiceType.WRAPPER.id(), bungeeSetting.getBungeeFavicon(),
                                    bungeeSetting.getMotd(), bungeeSetting.getMotd2(), bungeeSetting.getSlots());
                            connection.sendData(packetData);
                            Wrapper.getInstance().sendToMaster(this);
                            host = "localhost";
                            sendServerRegister(connection, serviceType);
                        }
                        proxyServer.started(connection);
                    } else {
                        proxyGroup.addServer(id).started(null);
                        host = dataContainer.read();
                    }
                }
                Wrapper.getInstance().broadcastPacket(this);
                break;
            case MASTER:
                final BungeeSetting bungeeSetting = new BungeeSetting(dataContainer.read(),
                        dataContainer.read(), dataContainer.read(), dataContainer.read());
                Wrapper.getInstance().getSetting().setBungeeSetting(bungeeSetting);
                final DataContainer container = new DataContainer();
                container.addAll(getId(), ServiceType.WRAPPER.id(), bungeeSetting.getBungeeFavicon(),
                        bungeeSetting.getMotd(), bungeeSetting.getMotd2(), bungeeSetting.getSlots());
                Wrapper.getInstance().getProxyManager().sendPacket(container);
                break;
            default:
                break;
        }
    }

    @Override
    public int getId() {
        return 0x0;
    }

    private void sendServerRegister(Connection connection, ServiceType direction) {
        for(GroupInstance<?> group : Wrapper.getInstance().getProxyManager().getGroups().values()) {
            final DataContainer dataContainer = new DataContainer();
            new PacketUpdateGroup(group, true).write(dataContainer, direction);
            connection.sendData(dataContainer);
            for(ServerInstance<?> server : group.getServers()) {
                final DataContainer dataContainer2 = new DataContainer();
                if(server.isStarted()) {
                    new PacketRegister(group.getName(), ServiceType.PROXY, server.getId()).write(dataContainer2,
                            direction);
                }else {
                    new PacketStartingInstance(group.getName(), server.getId(), true).write(dataContainer2, direction);
                }
                connection.sendData(dataContainer2);
            }
        }
        for(GroupInstance<?> group : Wrapper.getInstance().getServerManager().getGroups().values()) {
            final DataContainer dataContainer = new DataContainer();
            new PacketUpdateGroup(group, false).write(dataContainer,
                    direction);
            connection.sendData(dataContainer);
            for(ServerInstance<?> server : group.getServers()) {
                final DataContainer dataContainer2 = new DataContainer();
                if(server.isStarted()) {
                    new PacketRegister(group.getName(), ServiceType.MINECRAFT, server.getId(),
                            server instanceof LocalServerInstance<?> ? "localhost" : ((BukkitServerImpl) server).getHost(),
                            server instanceof LocalServerInstance<?> ? ((LocalServerInstance<?>) server).getPort()
                                    : ((BukkitServerImpl) server).getPort()).write(dataContainer2, direction);
                }else {
                    new PacketStartingInstance(group.getName(), server.getId(), true).write(dataContainer2, direction);
                }
                connection.sendData(dataContainer2);
            }
        }
    }

}
