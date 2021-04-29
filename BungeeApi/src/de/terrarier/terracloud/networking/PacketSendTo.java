package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.utils.Triple;

public class PacketSendTo extends Packet {

    private final Packet payload;
    private final Triple<String, Integer, Boolean>[] targets;

    public PacketSendTo(Packet payload, Triple<String, Integer, Boolean>... targets) {
        this.payload = payload;
        this.targets = targets;
    }

    @Override
    protected void write0(DataContainer dataContainer) {
        dataContainer.add(targets.length);
        for (Triple<String, Integer, Boolean> target : targets) {
            dataContainer.addAll(target.getFirst(), target.getSecond(), target.getThird());
        }
        payload.write(dataContainer);
    }

    @Override
    protected void read0(DataContainer dataContainer) {}

    @Override
    public int getId() {
        return 0x9;
    }

}
