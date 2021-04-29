package de.terrarier.terracloud.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomPayloadReceivedEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final int id;
    private final String channel;
    private final Object[] data;

    public CustomPayloadReceivedEvent(int id, String channel, Object[] data) {
        this.id = id;
        this.channel = channel;
        this.data = data;
    }

    public int getId() {
        return this.id;
    }

    public String getChannel() {
        return this.channel;
    }

    public Object[] getData() {
        return this.data;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
