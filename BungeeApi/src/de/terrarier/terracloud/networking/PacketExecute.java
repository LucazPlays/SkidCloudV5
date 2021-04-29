package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;

public final class PacketExecute extends Packet {

    private final String targetGroup;
    private final int targetId;
    private final boolean proxy;
    private final byte[] command;

    public PacketExecute(String targetGroup, int targetId, boolean proxy, byte[] command) {
        this.targetGroup = targetGroup;
        this.targetId = targetId;
        this.proxy = proxy;
        this.command = command;
    }

    @Override
    protected void write0(DataContainer dataContainer) {
        dataContainer.addAll(targetGroup, targetId, proxy, command);
    }

    @Override
    protected void read0(DataContainer dataContainer) {}

    @Override
    public int getId() {
        return 0x7;
    }

}
