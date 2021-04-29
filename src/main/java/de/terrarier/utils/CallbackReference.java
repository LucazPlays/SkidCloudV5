package de.terrarier.utils;

import de.terrarier.Wrapper;
import de.terrarier.lib.CustomPayloadUtil;
import de.terrarier.server.GroupInstance;
import de.terrarier.server.ServerInstance;

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
        GroupInstance<?> group = proxy ? Wrapper.getInstance().getProxyManager().getGroup(this.serverGroup)
                : Wrapper.getInstance().getServerManager().getGroup(this.serverGroup);
        if(group != null) {
            ServerInstance<?> server = group.getServer(this.serverId);
            if(server != null) {
                CustomPayloadUtil.triggerCallback(server.getConnection(), uuid, serverId);
            }
        }
    }

}
