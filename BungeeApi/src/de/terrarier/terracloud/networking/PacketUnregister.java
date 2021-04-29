package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.BungeeCloudApi;
import de.terrarier.terracloud.event.InstanceStartedEvent;
import de.terrarier.terracloud.event.InstanceStoppedEvent;
import net.md_5.bungee.BungeeCord;

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
    protected void write0(DataContainer dataContainer) {
        dataContainer.addAll(serviceType.id(), group, id);
    }

    @Override
    protected void read0(DataContainer dataContainer) {
        final byte serviceId = dataContainer.read();
        serviceType = ServiceType.SERVICE_TYPES[serviceId];
        group = dataContainer.read();
        switch (serviceType) {
            case PROXY:
                id = dataContainer.read();
                BungeeCord.getInstance().getPluginManager().callEvent(new InstanceStoppedEvent(
                        BungeeCloudApi.getInstance().getProxyManager().getGroup(group).removeServer(id)));
                break;
            case MINECRAFT:
                id = dataContainer.read();
                BungeeCord.getInstance().getPluginManager().callEvent(new InstanceStoppedEvent(
                        BungeeCloudApi.getInstance().getServerManager().getGroup(group).removeServer(id)));
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
