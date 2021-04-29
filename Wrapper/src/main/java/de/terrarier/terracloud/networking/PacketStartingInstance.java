package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;

public class PacketStartingInstance extends Packet {

    private String groupName;
    private int id;
    private boolean proxy;

    public PacketStartingInstance() {}

    public PacketStartingInstance(String groupName, int id, boolean proxy) {
        this.groupName = groupName;
        this.id = id;
        this.proxy = proxy;
    }

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.addAll(proxy, groupName, id);
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        proxy = dataContainer.read();
        groupName = dataContainer.read();
        id = dataContainer.read();
        final InstanceManager<?, ?> instanceManager = proxy ? Wrapper.getInstance().getProxyManager()
                : Wrapper.getInstance().getServerManager();
        final GroupInstance<?> group = instanceManager.getGroup(groupName);
        if(group != null) {
            group.addServer(id);
        }
    }

    @Override
    public int getId() {
        return 0x5;
    }

}
