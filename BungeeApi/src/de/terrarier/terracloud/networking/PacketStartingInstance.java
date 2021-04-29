package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.BungeeCloudApi;
import de.terrarier.terracloud.event.InstanceStartingEvent;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.ServerInstance;
import net.md_5.bungee.BungeeCord;

public class PacketStartingInstance extends Packet {

    @Override
    protected void write0(DataContainer dataContainer) {}

    @Override
    protected void read0(DataContainer dataContainer) {
        final boolean proxy = dataContainer.read();
        final String groupName = dataContainer.read();
        final int id = dataContainer.read();
        final InstanceManager<?, ?> instanceManager = proxy ? BungeeCloudApi.getInstance().getProxyManager() :
                BungeeCloudApi.getInstance().getServerManager();
        final GroupInstance<?> group = instanceManager.getGroup(groupName);
        if(group != null) {
            final ServerInstance<?> server = group.addServer(id);
            BungeeCord.getInstance().getPluginManager().callEvent(new InstanceStartingEvent(server));
        }
    }

    @Override
    public int getId() {
        return 0x5;
    }

}
