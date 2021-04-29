package de.terrarier.terracloud.server.wrapper;

import de.terrarier.netlistening.Connection;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.ServerGroup;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class WrapperManager {

    private final Map<String, Wrapper> wrappers = new HashMap<>();
    private final Map<Connection, String> wrapperNames = new HashMap<>();
    private final Queue<Task> taskQueue = new ConcurrentLinkedQueue<>();

    public WrapperManager() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                while(!taskQueue.isEmpty()) {
                    final Task task = taskQueue.peek();
                    if(!hasAnyWrapperEnoughMemory(task.memory)) {
                        break;
                    }
                    taskQueue.poll();
                    task.task.run();
                }
            }
        }, 2000, 2000);
    }

    public void registerWrapper(String name, int cores, Connection connection, long freeMemory) {
        wrapperNames.put(connection, name);
        wrappers.put(name, new Wrapper(name, cores, connection, freeMemory));
        Logger.log("Der Wrapper " + name + " hat sich registriert.", LogType.INFO);
    }

    public boolean isRegistered(Connection connection) {
        return wrapperNames.containsKey(connection);
    }

    public Wrapper fromConnection(Connection connection) {
        return wrappers.get(wrapperNames.get(connection));
    }

    public Wrapper unregisterWrapper(Connection connection) {
        final String name = wrapperNames.remove(connection);
        final Wrapper wrapper = wrappers.remove(name);
        for(ProxyGroup group : Master.getInstance().getProxyManager().getGroups().values()) {
            for(ProxyServer server : group.getServers()) {
                if(server.getWrapper().getName().equals(name)) {
                    Master.getInstance().getProxyManager().stopServer(server);
                }
            }
        }
        for(ServerGroup group : Master.getInstance().getServerManager().getGroups().values()) {
            for(BukkitServer server : group.getServers()) {
                if(server.getWrapper().getName().equals(name)) {
                    Master.getInstance().getServerManager().stopServer(server);
                }
            }
        }
        return wrapper;
    }

    public Wrapper getBestSuitableWrapper(int memory) {
        double bestLoad = 0D;
        Wrapper best = null;
        for(Wrapper wrapper : wrappers.values()) {
            if(wrapper.getFreeMemory() > 1024 + memory && wrapper.getCores() / wrapper.getCpuLoad() > bestLoad) {
                bestLoad = wrapper.getCores() / wrapper.getCpuLoad();
                best = wrapper;
            }
        }
        if(best != null) {
            best.reduceMemory(memory);
        }
        return best;
    }

    private boolean hasAnyWrapperEnoughMemory(int memory) {
        for(Wrapper wrapper : wrappers.values()) {
            if(wrapper.getFreeMemory() > 1024 + memory) {
                return true;
            }
        }
        return false;
    }

    public void addTask(Runnable task, int memory) {
        taskQueue.add(new Task(task, memory));
    }

    public Set<Wrapper> getWrappers() {
        return new HashSet<>(wrappers.values());
    }

    private class Task {

        private final Runnable task;
        private final int memory;

        private Task(Runnable task, int memory) {
            this.task = task;
            this.memory = memory;
        }
    }

}
