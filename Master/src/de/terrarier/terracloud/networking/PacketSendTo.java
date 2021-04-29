package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.utils.Triple;

public class PacketSendTo extends Packet {

    private Packet payload;
    private Triple<String, Integer, Boolean>[] targets;

    public PacketSendTo() {}

    public PacketSendTo(Packet payload, Triple<String, Integer, Boolean>... targets) {
        this.payload = payload;
        this.targets = targets;
    }

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.add(targets.length);
        for (Triple<String, Integer, Boolean> target : targets) {
            dataContainer.addAll(target.getFirst(), target.getSecond(), target.getThird());
        }
        payload.write(dataContainer, direction);
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        final DataContainer payload = dataContainer.subContainer((int) dataContainer.read() * 3);
        while(dataContainer.remainingReads() < payload.getSize()) {
            final String name = dataContainer.read();
            final int id = dataContainer.read();
            final boolean proxy = dataContainer.read();
            final InstanceManager<?, ?> manager = proxy ? Master.getInstance().getProxyManager() : Master.getInstance().getServerManager();
            final GroupInstance<?> group = manager.getGroup(name);
            if(group != null) {
                final ServerInstance<?> instance = group.getServer(id);
                instance.getWrapper().getConnection().sendData(payload);
            }
        }
    }

    @Override
    public int getId() {
        return 0x9;
    }

}
