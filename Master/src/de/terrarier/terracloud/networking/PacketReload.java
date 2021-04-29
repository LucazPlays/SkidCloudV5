package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Master;

public class PacketReload extends Packet {

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {}

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        Master.getInstance().reload();
    }

    @Override
    public int getId() {
        return 0x12;
    }

}
