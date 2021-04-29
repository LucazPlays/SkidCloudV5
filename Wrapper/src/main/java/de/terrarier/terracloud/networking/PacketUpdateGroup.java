package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.spigot.ServerGroup;

import java.io.File;

public class PacketUpdateGroup extends Packet {

    private String group;
    private boolean proxy;
    private GroupUpdateType updateType;
    private GroupAttributeType groupAttributeType;
    private Object groupAttribute;
    private GroupInstance<?> newGroup;

    public PacketUpdateGroup(String group, boolean proxy) {
        this.group = group;
        this.proxy = proxy;
        updateType = GroupUpdateType.DELETE;
    }

    public PacketUpdateGroup(GroupInstance<?> group, boolean proxy) {
        this.group = group.getName();
        this.proxy = proxy;
        updateType = GroupUpdateType.CREATE;
        newGroup = group;
    }

    public PacketUpdateGroup(String group, boolean proxy, GroupAttributeType attributeType, Object attribute) {
        this.group = group;
        this.proxy = proxy;
        updateType = GroupUpdateType.ATTRIBUTE;
        groupAttributeType = attributeType;
        groupAttribute = attribute;
    }

    public PacketUpdateGroup() {}

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.addAll(group, proxy, updateType.id());
        switch (updateType) {
            case ATTRIBUTE:
                dataContainer.addAll(groupAttributeType.id(), groupAttribute);
                break;
            case CREATE:
                dataContainer.addAll(newGroup.getName(), newGroup.getServerCount(), newGroup.isDynamic(), newGroup.getMemory());
                break;
        }
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        group = dataContainer.read();
        proxy = dataContainer.read();
        updateType = GroupUpdateType.fromId(dataContainer.read());
        if(connection.getId() == Wrapper.getInstance().getMasterConnection().getConnection().getId()) {
            switch (updateType) {
                case ATTRIBUTE:
                    groupAttributeType = GroupAttributeType.fromId(dataContainer.read());
                    groupAttribute = dataContainer.read();
                    // TODO: Implement update logic!
                    break;
                case CREATE:
                    InstanceManager<?, ?> manager = proxy ? Wrapper.getInstance().getProxyManager()
                            : Wrapper.getInstance().getServerManager();
                    manager.getGroups().put(group, createGroup(proxy, dataContainer));
                    break;
                case DELETE:
                    manager = proxy ? Wrapper.getInstance().getProxyManager()
                            : Wrapper.getInstance().getServerManager();
                    manager.deleteGroup(group);
                    break;
                default:
                    break;
            }
        }else {
            Wrapper.getInstance().sendToMaster(this);
        }
    }

    private static <G> G createGroup(boolean proxy, DataContainer dataContainer) {
        final String name = dataContainer.read();
        final int serverCount = dataContainer.read();
        final boolean dynamic = dataContainer.read();
        final int memory = dataContainer.read();
        final File file = new File(proxy ? Wrapper.getInstance().getProxyManager().getGroupPath(dynamic, name) :
                Wrapper.getInstance().getServerManager().getGroupPath(dynamic, name));
        if(!file.exists()) {
            file.mkdir();
        }
        return proxy ? (G) new ProxyGroup(name, serverCount, dynamic, memory)
                : (G) new ServerGroup(name, serverCount, dynamic, memory);
    }

    @Override
    public int getId() {
        return 0x3;
    }

    enum GroupUpdateType {

        DELETE, CREATE, ATTRIBUTE;

        static final GroupUpdateType[] UPDATE_TYPES = values();

        static GroupUpdateType fromId(byte id) {
            return UPDATE_TYPES[id];
        }

        byte id() {
            return (byte) ordinal();
        }

    }

    enum GroupAttributeType {

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
