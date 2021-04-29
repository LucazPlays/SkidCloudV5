package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Master;

public final class PacketStartInstance extends Packet {

    private String groupName;
    private int id;
    private boolean proxy;

    PacketStartInstance() {}

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
            Master.getInstance().getProxyManager().startServer(Master.getInstance().getProxyManager().getGroup(groupName), id);
        }else {
            Master.getInstance().getServerManager().startServer(Master.getInstance().getServerManager().getGroup(groupName), id);
        }
    }

    @Override
    public int getId() {
        return 0x6;
    }

}
