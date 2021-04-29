package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;

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
        if(serviceType != ServiceType.MASTER) {
            dataContainer.add(id);
        }
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        final byte serviceId = dataContainer.read();
        serviceType = ServiceType.SERVICE_TYPES[serviceId];
        group = dataContainer.read();
        switch (serviceType) {
            case PROXY:
                id = dataContainer.read();
                Master.getInstance().getProxyManager().getGroup(group).removeServer(id);
                Logger.log("Der ProxyServer " + group + "-" + id + " wurde gestoppt!", LogType.INFO);
                break;
            case MINECRAFT:
                id = dataContainer.read();
                Master.getInstance().getServerManager().getGroup(group).removeServer(id);
                Logger.log("Der Server " + group + "-" + id + " wurde gestoppt!", LogType.INFO);
                break;
            default:
                break;
        }
        Master.getInstance().broadcastPacket(this, null);
    }

    @Override
    public int getId() {
        return 0x10;
    }
}
