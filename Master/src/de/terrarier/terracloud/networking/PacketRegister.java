package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.server.proxy.BungeeSetting;

import java.util.UUID;

public class PacketRegister extends Packet {

    private ServiceType serviceType;
    private String serviceName;
    private int id;
    private int port;
    private String host;

    public PacketRegister(String serviceName) {
        this.serviceName = serviceName;
    }

    public PacketRegister() {}

    public PacketRegister(String name, ServiceType type, int id) {
        serviceName = name;
        serviceType = type;
        this.id = id;
    }

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.addAll(serviceType.id(), serviceName, id);
        if(serviceType == ServiceType.MINECRAFT) {
            dataContainer.add(port);
        }
        dataContainer.add(host);
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        final byte serviceTypeId = dataContainer.read();
        serviceType = ServiceType.SERVICE_TYPES[serviceTypeId];
        switch (serviceType) {
            case WRAPPER:
                try {
                    final UUID key = dataContainer.read();
                    if (!Master.getInstance().getSetting().getKey().equals(key)) {
                        connection.disconnect();
                        Logger.log("A wrapper tried to register with an invalid key, it was blocked!", LogType.WARN);
                        return;
                    }
                } catch (Exception e) {
                    connection.disconnect();
                    Logger.log("A wrapper tried to register but an exception occurred while performing the check!", LogType.WARN);
                    e.printStackTrace();
                    return;
                }
                final DataContainer container = new DataContainer();
                final BungeeSetting bungeeSetting = Master.getInstance().getSetting().getBungeeSetting();
                container.addAll(getId(), ServiceType.MASTER.id(), bungeeSetting.getBungeeFavicon(),
                        bungeeSetting.getMotd(), bungeeSetting.getMotd2(), bungeeSetting.getSlots());
                connection.sendData(container);
                sendServerRegister(connection, ServiceType.WRAPPER);
                final String name = dataContainer.read();
                final int cores = dataContainer.read();
                final long memory = dataContainer.read();
                Master.getInstance().getWrapperManager().registerWrapper(name, cores, connection, memory);
                break;
            case PROXY:
                serviceName = dataContainer.read();
                id = dataContainer.read();
                host = dataContainer.read();
                Master.getInstance().getProxyManager().getGroup(serviceName).getServer(id)
                        .started();
                Master.getInstance().broadcastPacket(this, Master.getInstance().getWrapperManager().fromConnection(connection));
                break;
            case MINECRAFT:
                serviceName = dataContainer.read();
                id = dataContainer.read();
                port = dataContainer.read();
                host = dataContainer.read();
                Master.getInstance().getServerManager().getGroup(serviceName).getServer(id)
                        .started();
                Master.getInstance().broadcastPacket(this, Master.getInstance().getWrapperManager().fromConnection(connection));
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
        for(GroupInstance<?> group : Master.getInstance().getProxyManager().getGroups().values()) {
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
        for(GroupInstance<?> group : Master.getInstance().getServerManager().getGroups().values()) {
            final DataContainer dataContainer = new DataContainer();
            new PacketUpdateGroup(group, false).write(dataContainer,
                    direction);
            connection.sendData(dataContainer);
            for(ServerInstance<?> server : group.getServers()) {
                final DataContainer dataContainer2 = new DataContainer();
                if(server.isStarted()) {
                    new PacketRegister(group.getName(), ServiceType.MINECRAFT, server.getId()).write(
                            dataContainer2, direction);
                }else {
                    new PacketStartingInstance(group.getName(), server.getId(), true).write(dataContainer2, direction);
                }
                connection.sendData(dataContainer2);
            }
        }
    }

}
