package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Bootstrap;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.proxy.ProxyServer;

public class PacketUnregister extends Packet {

    private String group;
    private ServiceType serviceType;
    private int id;

    public PacketUnregister() {}

    public PacketUnregister(String group, ServiceType serviceType) {
        this.group = group;
        this.serviceType = serviceType;
    }

    public PacketUnregister(String group, ServiceType serviceType, int id) {
        this(group, serviceType);
        this.id = id;
    }

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.addAll(serviceType.id(), group);
        if(serviceType != ServiceType.WRAPPER) {
            dataContainer.add(id);
        }
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        final byte serviceId = dataContainer.read();
        serviceType = ServiceType.SERVICE_TYPES[serviceId];
        switch (serviceType) {
            case PROXY:
                ProxyServer proxy = Wrapper.getInstance().getProxyManager().getGroup(dataContainer.read()).removeServer(dataContainer.read());
                if(proxy instanceof LocalServerInstance) {
                    Wrapper.getInstance().getProxyManager().stopServer(proxy,
                            Wrapper.getInstance().getMasterConnection().getConnection().getId() == connection.getId());
                }
                break;
            case MINECRAFT:
                Wrapper.getInstance().getServerManager().getGroup(dataContainer.read()).removeServer(dataContainer.read());
                break;
            case MASTER:
                Logger.log("The master was shutdown, thus the wrapper will shutdown too.", LogType.WARN);
                Bootstrap.shutdown();
                break;
            default:
                break;
        }
    }

    @Override
    public int getId() {
        return 0x10;
    }
}
