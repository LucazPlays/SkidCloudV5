package de.terrarier.terracloud.event;

import net.md_5.bungee.api.plugin.Event;

public class CustomPayloadReceivedEvent extends Event {

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
        return data;
    }

}
