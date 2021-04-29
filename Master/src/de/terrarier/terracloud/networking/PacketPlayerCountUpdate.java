package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;

public class PacketPlayerCountUpdate extends Packet {

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {

    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {

    }

    @Override
    public int getId() {
        return 0x2;
    }
}
