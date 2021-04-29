package de.terrarier.terracloud.event;

import de.terrarier.terracloud.server.ServerInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class InstanceCountUpdateEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

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

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
