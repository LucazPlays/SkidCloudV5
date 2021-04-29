package de.terrarier.terracloud.event;

import de.terrarier.terracloud.server.ServerInstance;
import net.md_5.bungee.api.plugin.Event;

public final class InstanceCountUpdateEvent extends Event {

    private final ServerInstance<?> instance;
    private final int count;

    public InstanceCountUpdateEvent(ServerInstance<?> instance, int count) {
        this.instance = instance;
        this.count = count;
    }

    public ServerInstance<?> getInstance() {
        return instance;
    }

    public int getCount() {
        return count;
    }

}
