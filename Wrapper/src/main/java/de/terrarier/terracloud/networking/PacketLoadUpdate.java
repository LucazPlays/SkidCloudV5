package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.utils.SystemUtil;

public class PacketLoadUpdate extends Packet {

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.addAll(SystemUtil.cpuUsage(), SystemUtil.systemMemory());
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {}

    @Override
    public int getId() {
        return 0x4;
    }

}
