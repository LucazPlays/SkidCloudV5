package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.BungeeCloudApi;
import de.terrarier.terracloud.event.InstanceCountUpdateEvent;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.ServerInstance;
import net.md_5.bungee.BungeeCord;

public class PacketPlayerCountUpdate extends Packet {

    private int playerCount;

    public PacketPlayerCountUpdate() {}

    public PacketPlayerCountUpdate(int playerCount) {
        this.playerCount = playerCount;
    }

    @Override
    protected void write0(DataContainer dataContainer) {
        dataContainer.addAll(BungeeCloudApi.SERVER_GROUP, BungeeCloudApi.SERVER_ID, true, playerCount, BungeeCord.getInstance().getConfig().getPlayerLimit());
    }

    @Override
    protected void read0(DataContainer dataContainer) {
        final String groupName = dataContainer.read();
        final int serverId = dataContainer.read();
        final boolean proxy = dataContainer.read();
        final InstanceManager<?, ?> instanceManager = proxy ? BungeeCloudApi.getInstance().getProxyManager()
                : BungeeCloudApi.getInstance().getServerManager();
        final GroupInstance<?> group = instanceManager.getGroup(groupName);
        if(group != null) {
            final ServerInstance<?> server = group.getServer(serverId);
            if(server != null) {
                final int players = dataContainer.read();
                server.updatePlayerCount(players, dataContainer.read());
                BungeeCord.getInstance().getPluginManager().callEvent(new InstanceCountUpdateEvent(server, players));
            }
        }
    }

    @Override
    public int getId() {
        return 0x2;
    }
}
