package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.event.CustomPayloadReceivedEvent;
import de.terrarier.terracloud.lib.BitUtil;
import de.terrarier.terracloud.lib.CustomPayloadUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PacketCustomPayload extends Packet {

    @Override
    protected void write0(DataContainer dataContainer) {}

    @Override
    protected void read0(DataContainer data) {
        final CustomPayloadUtil.PayloadBuilder payload = CustomPayloadUtil.getBuilder();
        payload.read(data);

        switch (payload.getId()) {
            case -2:
                byte messageSetting = (byte) payload.getData()[0];
                String message = (String) payload.getData()[1];
                String receiver = null;
                String permission = null;

                if (BitUtil.isBitSet(messageSetting, 1))
                    receiver = (String) payload.getData()[2];

                if (BitUtil.isBitSet(messageSetting, 2))
                    permission = (String) payload.getData()[3];

                if (receiver == null) {
                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                        if (permission == null || player.hasPermission(permission)) {
                            player.sendMessage(message);
                        }
                    }
                    return;
                }

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(receiver);

                if(player == null || !player.isConnected()) {
                    return;
                }

                player.sendMessage(message);
                break;
            case -8:
                // TODO: Add callback function
                break;
            default:
                ProxyServer.getInstance().getPluginManager().callEvent(new CustomPayloadReceivedEvent(payload.getId(), payload.getChannel(), payload.getData()));
                break;
        }
    }

    @Override
    public int getId() {
        return 0x11;
    }

}
