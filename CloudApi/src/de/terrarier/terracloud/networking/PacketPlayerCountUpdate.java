package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.CloudApi;
import de.terrarier.terracloud.event.InstanceCountUpdateEvent;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.ServerInstance;
import org.bukkit.Bukkit;

public class PacketPlayerCountUpdate extends Packet {

    private int playerCount;

    public PacketPlayerCountUpdate() {}

    public PacketPlayerCountUpdate(int playerCount) {
        this.playerCount = playerCount;
    }

    @Override
    protected void write0(DataContainer dataContainer) {
        dataContainer.addAll(CloudApi.SERVER_GROUP, CloudApi.SERVER_ID, false, playerCount, Bukkit.getMaxPlayers());
    }

    @Override
    protected void read0(DataContainer dataContainer) {
        final String groupName = dataContainer.read();
        final int serverId = dataContainer.read();
        final boolean proxy = dataContainer.read();
        final InstanceManager<?, ?> instanceManager = proxy ? CloudApi.getInstance().getProxyManager()
                : CloudApi.getInstance().getServerManager();
        final GroupInstance<?> group = instanceManager.getGroup(groupName);
        if(group != null) {
            final ServerInstance<?> server = group.getServer(serverId);
            if(server != null) {
                final int players = dataContainer.read();
                server.updatePlayerCount(players, dataContainer.read());
                Bukkit.getPluginManager().callEvent(new InstanceCountUpdateEvent(server, players));
            }
        }
    }

    @Override
    public int getId() {
        return 0x2;
    }
}
