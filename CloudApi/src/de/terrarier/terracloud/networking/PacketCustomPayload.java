package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.event.CustomPayloadReceivedEvent;
import de.terrarier.terracloud.lib.CustomPayloadUtil;
import org.bukkit.Bukkit;

public class PacketCustomPayload extends Packet {

    @Override
    protected void write0(DataContainer dataContainer) {}

    @Override
    protected void read0(DataContainer data) {
        final CustomPayloadUtil.PayloadBuilder payload = CustomPayloadUtil.getBuilder();
        payload.read(data);

        Bukkit.getPluginManager().callEvent(new CustomPayloadReceivedEvent(payload.getId(), payload.getChannel(), payload.getData()));
    }

    @Override
    public int getId() {
        return 0x11;
    }

}
