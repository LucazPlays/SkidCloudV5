package de.terrarier.terracloud.event;

import de.terrarier.terracloud.server.ServerInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InstanceStartedEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final ServerInstance<?> instance;

    public InstanceStartedEvent(ServerInstance<?> instance) {
        this.instance = instance;
    }

    public ServerInstance<?> getInstance() {
        return instance;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
