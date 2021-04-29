package de.terrarier.terracloud.server;

import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.server.wrapper.Wrapper;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GroupInstance<T extends ServerInstance<?>> {

    private final String name;
    private final int serverCount;
    private final boolean dynamic;
    private final int memory;
    protected final ConcurrentHashMap<Integer, T> servers = new ConcurrentHashMap<>();
    private boolean deleted;

    public GroupInstance(String name, int serverCount, boolean dynamic, int memory) {
        this.name = name;
        this.serverCount = serverCount;
        this.dynamic = dynamic;
        this.memory = memory;
    }

    public String getName() {
        return name;
    }

    public int getServerCount() {
        return this.serverCount;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public int getMemory() {
        return this.memory;
    }

    public int getNextId() {
        int highest = 0;
        for(T server : servers.values()) {
            if(server.getId() > highest) {
                highest = server.getId();
            }
        }
        for(int i = 1; i < highest + 1; i++) {
            if(getServer(i) == null) {
                return i;
            }
        }
        return highest + 1;
    }

    public T getServer(int id) {
        return this.servers.get(id);
    }

    public abstract T addServer(int id, Wrapper wrapper);

    protected void addServer(T server) {
        this.servers.put(server.getId(), server);
    }

    public T removeServer(int id) {
        return this.servers.remove(id);
    }

    public Collection<T> getServers() {
        return this.servers.values();
    }

    public void sendData(DataContainer data) {
        for (ServerInstance<?> server : this.servers.values()) {
            if (server.isStarted())
                server.getWrapper().getConnection().sendData(data);
        }
    }

    public void delete() {
        deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
