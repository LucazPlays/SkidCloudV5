package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;

public class PacketReload extends Packet {

    @Override
    protected void write0(DataContainer dataContainer) {}

    @Override
    protected void read0(DataContainer dataContainer) {}

    @Override
    public int getId() {
        return 0x12;
    }

}
