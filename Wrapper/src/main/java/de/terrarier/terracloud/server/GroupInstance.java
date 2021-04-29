package de.terrarier.terracloud.server;

import de.terrarier.netlistening.api.DataContainer;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GroupInstance<T extends ServerInstance<?>> {

    private final String name;
    private final int serverCount;
    private final boolean dynamic;
    private final int memory;
    private final ConcurrentHashMap<Integer, T> servers = new ConcurrentHashMap<>();

    public GroupInstance(String name, int serverCount, boolean dynamic, int memory) {
        this.name = name;
        this.serverCount = serverCount;
        this.dynamic = dynamic;
        this.memory = memory;
    }

    public String getName() {
        return this.name;
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
        for(T server : this.servers.values()) {
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
        return servers.get(id);
    }

    protected void addServer(T server) {
        servers.put(server.getId(), server);
    }

    public abstract T addServer(int id);

    public T removeServer(int id) {
        return servers.remove(id);
    }

    public Collection<T> getServers() {
        return servers.values();
    }

    public void sendData(DataContainer data) {
        for (ServerInstance<?> server : servers.values()) {
            if (server.isStarted() && server instanceof LocalServerInstance) {
                ((LocalServerInstance<?>) server).getConnection().sendData(data);
            }
        }
    }

}
