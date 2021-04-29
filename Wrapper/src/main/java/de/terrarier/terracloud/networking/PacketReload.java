package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.server.proxy.BungeeSetting;

public class PacketReload extends Packet {

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {}

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        if(Wrapper.getInstance().getMasterConnection().getConnection().getId() != connection.getId()) {
            Wrapper.getInstance().sendToMaster(this);
        }else {
            final DataContainer container = new DataContainer();
            final BungeeSetting bungeeSetting = Wrapper.getInstance().getSetting().getBungeeSetting();
            container.addAll(0x0, ServiceType.WRAPPER.id(), bungeeSetting.getBungeeFavicon(), bungeeSetting.getMotd(),
                    bungeeSetting.getMotd2(), bungeeSetting.getSlots());
            Wrapper.getInstance().getProxyManager().sendPacket(container);
        }
    }

    @Override
    public int getId() {
        return 0x12;
    }

}
