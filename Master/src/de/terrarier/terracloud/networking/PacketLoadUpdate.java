package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.utils.SystemUtil;

public class PacketLoadUpdate extends Packet {

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {}

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        final double cpuLoad = dataContainer.read();
        final long memory = dataContainer.read();
        Master.getInstance().getWrapperManager().fromConnection(connection).updateLoad(cpuLoad, memory / SystemUtil.MB);
    }

    @Override
    public int getId() {
        return 0x4;
    }

}
