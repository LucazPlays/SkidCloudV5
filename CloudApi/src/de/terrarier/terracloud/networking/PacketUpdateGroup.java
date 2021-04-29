package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.CloudApi;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.spigot.ServerGroup;

public class PacketUpdateGroup extends Packet {

    private String group;
    private boolean proxy;
    private GroupUpdateType updateType;
    private GroupAttributeType groupAttributeType;
    private Object groupAttribute;

    public PacketUpdateGroup(String group, boolean proxy, GroupUpdateType updateType) {
        this.group = group;
        this.proxy = proxy;
        this.updateType = updateType;
    }

    public PacketUpdateGroup(String group, boolean proxy, GroupAttributeType attributeType, Object attribute) {
        this.group = group;
        this.proxy = proxy;
        this.updateType = GroupUpdateType.ATTRIBUTE;
        groupAttributeType = attributeType;
        groupAttribute = attribute;
    }

    public PacketUpdateGroup() {}

    @Override
    protected void write0(DataContainer dataContainer) {
        dataContainer.addAll(group, proxy, updateType.id());
        if(updateType == GroupUpdateType.ATTRIBUTE) {
            dataContainer.addAll(groupAttributeType.id(), groupAttribute);
        }
    }

    @Override
    protected void read0(DataContainer dataContainer) {
        group = dataContainer.read();
        proxy = dataContainer.read();
        updateType = GroupUpdateType.fromId(dataContainer.read());
        switch (updateType) {
            case ATTRIBUTE:
                groupAttributeType = GroupAttributeType.fromId(dataContainer.read());
                groupAttribute = dataContainer.read();
                // TODO: Implement update logic!
                break;
            case CREATE:
                InstanceManager<?, ?> manager = proxy ? CloudApi.getInstance().getProxyManager()
                        : CloudApi.getInstance().getServerManager();
                manager.getGroups().put(group, createGroup(proxy, dataContainer));
                System.out.println("register: " + group);
                break;
            case DELETE:
                manager = proxy ? CloudApi.getInstance().getProxyManager()
                        : CloudApi.getInstance().getServerManager();
                manager.getGroups().remove(group);
                break;
            default:
                break;
        }
    }

    private static <G> G createGroup(boolean proxy, DataContainer dataContainer) {
        return proxy ? (G) new ProxyGroup(dataContainer.read(), dataContainer.read(), dataContainer.read(), dataContainer.read())
                : (G) new ServerGroup(dataContainer.read(), dataContainer.read(), dataContainer.read(), dataContainer.read());
    }

    @Override
    public int getId() {
        return 0x3;
    }

    public enum GroupUpdateType {

        DELETE, CREATE, ATTRIBUTE;

        static final GroupUpdateType[] UPDATE_TYPES = values();

        static GroupUpdateType fromId(byte id) {
            return UPDATE_TYPES[id];
        }

        byte id() {
            return (byte) ordinal();
        }

    }

    public enum GroupAttributeType {

        NAME, MEMORY, SERVERS, MODE;

        static final GroupAttributeType[] ATTRIBUTE_TYPES = values();

        static GroupAttributeType fromId(byte id) {
            return ATTRIBUTE_TYPES[id];
        }

        byte id() {
            return (byte) ordinal();
        }

    }

}
