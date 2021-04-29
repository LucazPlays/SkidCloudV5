package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;

public abstract class Packet {

    public void write(DataContainer dataContainer, ServiceType direction) {
        dataContainer.add(getId());
        write0(dataContainer, direction);
    }

    protected abstract void write0(DataContainer dataContainer, ServiceType direction);

    public static void read(DataContainer dataContainer, Connection connection) {
        final int id = dataContainer.read();
        final Packet packet = fromId(id);
        packet.read0(dataContainer, connection);
    }

    protected abstract void read0(DataContainer dataContainer, Connection connection);

    public abstract int getId();

    public static Packet fromId(int id) {
        switch (id) {
            case 0x0:
                return new PacketRegister();
            case 0x2:
                return new PacketPlayerCountUpdate();
            case 0x3:
                return new PacketUpdateGroup();
            case 0x4:
                return new PacketLoadUpdate();
            case 0x5:
                return new PacketStartingInstance();
            case 0x6:
                return new PacketStartInstance();
            case 0x7:
                return new PacketExecute();
            case 0x8:
                return new PacketStopInstance();
            case 0x9:
                return new PacketSendTo();
            case 0x10:
                return new PacketUnregister();
            case 0x11:
                return new PacketCustomPayload();
            case 0x12:
                return new PacketReload();
        }
        return null;
    }

}
