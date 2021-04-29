package de.terrarier.terracloud.utils;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.ServerInstance;

import java.util.UUID;

public final class CallbackReference {

    private final boolean proxy;
    private final String serverGroup;
    private final int serverId;
    private final UUID uuid;

    public CallbackReference(boolean proxy, String serverGroup, int serverId, UUID uuid) {
        this.proxy = proxy;
        this.serverGroup = serverGroup;
        this.serverId = serverId;
        this.uuid = uuid;
    }

    public void triggerCallback(int serverId) {
        GroupInstance<?> group = proxy ? Master.getInstance().getProxyManager().getGroup(this.serverGroup)
                : Master.getInstance().getServerManager().getGroup(this.serverGroup);
        if(group != null) {
            ServerInstance<?> server = group.getServer(this.serverId);
            if(server != null) {
                // CustomPayloadUtil.triggerCallback(server.getConnection(), uuid, serverId); TODO: Implement this
            }
        }
    }

}
