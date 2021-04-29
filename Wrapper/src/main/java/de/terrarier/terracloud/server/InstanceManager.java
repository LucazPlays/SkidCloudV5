package de.terrarier.terracloud.server;

import de.terrarier.netlistening.api.DataContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InstanceManager<T extends ServerInstance<V>, V extends GroupInstance<T>> {

    protected final Map<String, V> groups = new ConcurrentHashMap<>();

    public abstract void startServer(V group);

    public abstract void stopServer(T server, boolean preventRestart);

    public Map<String, V> getGroups() {
        return groups;
    }

    public abstract void createGroup(V group);

    public T getServer(String groupName, int id) {
        return getGroup(groupName).getServer(id);
    }

    public V getGroup(String groupName) {
        return groups.get(groupName);
    }

    public abstract String getPath(boolean dynamic, String name, int id);

    public void sendPacket(DataContainer dataContainer) {
        for(V serverGroup : groups.values()) {
            serverGroup.sendData(dataContainer);
        }
    }

    public abstract boolean deleteGroup(String groupName);

}
