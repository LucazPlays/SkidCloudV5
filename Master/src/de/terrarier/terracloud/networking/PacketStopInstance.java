package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.ServerInstance;

public class PacketStopInstance extends Packet {

    // TODO: Implement CustomPayLoad like information exchange

    private String groupName;
    private int id;
    private boolean proxy;

    public PacketStopInstance() {}

    public PacketStopInstance(String groupName, int id, boolean proxy) {
        this.groupName = groupName;
        this.id = id;
        this.proxy = proxy;
    }

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.addAll(groupName, id, proxy);
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        groupName = dataContainer.read();
        id = dataContainer.read();
        proxy = dataContainer.read();
        InstanceManager manager = proxy ? Master.getInstance().getProxyManager() : Master.getInstance().getServerManager();
        final GroupInstance<?> group = manager.getGroup(groupName);
        if(group != null) {
            final ServerInstance<?> server = group.getServer(id);
            if(server != null) {
                manager.stopServer(server);
            }
        }
    }

    @Override
    public int getId() {
        return 0x8;
    }
}
