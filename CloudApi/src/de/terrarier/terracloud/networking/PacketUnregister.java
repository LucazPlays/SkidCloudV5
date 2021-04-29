package de.terrarier.terracloud.networking;

import com.mysql.jdbc.Buffer;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.CloudApi;
import de.terrarier.terracloud.event.InstanceStoppedEvent;
import org.bukkit.Bukkit;

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
        switch (serviceType) {
            case PROXY:
                Bukkit.getPluginManager().callEvent(new InstanceStoppedEvent(
                        CloudApi.getInstance().getProxyManager().getGroup(dataContainer.read()).removeServer(
                                dataContainer.read())));
                break;
            case MINECRAFT:
                Bukkit.getPluginManager().callEvent(new InstanceStoppedEvent(
                        CloudApi.getInstance().getServerManager().getGroup(dataContainer.read()).removeServer(
                                dataContainer.read())));
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
