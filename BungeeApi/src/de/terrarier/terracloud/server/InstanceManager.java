package de.terrarier.terracloud.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InstanceManager<T extends ServerInstance<V>, V extends GroupInstance<T>> {

    protected final Map<String, V> groups = new ConcurrentHashMap<>();

    public Map<String, V> getGroups() {
        return groups;
    }

    public T getServer(String groupName, int id) {
        return getGroup(groupName).getServer(id);
    }

    public V getGroup(String groupName) {
        return groups.get(groupName);
    }

}
