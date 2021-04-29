package de.terrarier.terracloud.event;

import de.terrarier.terracloud.server.ServerInstance;
import net.md_5.bungee.api.plugin.Event;

public class InstanceStartedEvent extends Event {

    private final ServerInstance<?> instance;

    public InstanceStartedEvent(ServerInstance<?> instance) {
        this.instance = instance;
    }

    public ServerInstance<?> getInstance() {
        return instance;
    }

}
