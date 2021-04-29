package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Wrapper;

public final class PacketStartInstance extends Packet {

    // TODO: Implement CustomPayLoad like information exchange

    private String groupName;
    private int id;
    private boolean proxy;

    public PacketStartInstance() {}

    public PacketStartInstance(String groupName, int id, boolean proxy) {
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
        if(proxy) {
            Wrapper.getInstance().getProxyManager().startServer(Wrapper.getInstance().getProxyManager()
                    .getGroup(groupName), id);
        }else {
            Wrapper.getInstance().getServerManager().startServer(Wrapper.getInstance().getServerManager()
                    .getGroup(groupName), id);
        }
    }

    @Override
    public int getId() {
        return 0x6;
    }

}
