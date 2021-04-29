package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.CloudApi;
import de.terrarier.terracloud.event.InstanceStartingEvent;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import org.bukkit.Bukkit;

public class PacketStartingInstance extends Packet {

    @Override
    protected void write0(DataContainer dataContainer) {}

    @Override
    protected void read0(DataContainer dataContainer) {
        final boolean proxy = dataContainer.read();
        final String groupName = dataContainer.read();
        final int id = dataContainer.read();
        final InstanceManager<?, ?> instanceManager = proxy ? CloudApi.getInstance().getProxyManager() :
                CloudApi.getInstance().getServerManager();
        final GroupInstance<?> group = instanceManager.getGroup(groupName);
        if(group != null) {
            Bukkit.getPluginManager().callEvent(new InstanceStartingEvent(group.addServer(id)));
        }
    }

    @Override
    public int getId() {
        return 0x5;
    }

}
