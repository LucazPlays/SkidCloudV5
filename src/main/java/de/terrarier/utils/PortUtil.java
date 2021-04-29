package de.terrarier.utils;

import de.terrarier.Wrapper;
import de.terrarier.server.GroupInstance;
import de.terrarier.server.ServerInstance;

import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;

public class PortUtil {

/*
    private static final AtomicInteger cached = new AtomicInteger();

    static {
        tryCachePort();
    }

    public static int freePort() {
        int port = _freePort();
        while (port == 0)
            port = _freePort();
        return port;
    }

    private static int _freePort() {
        int localCached = cached.getAndSet(0);
        boolean valid = (localCached != 0 && !checkUsageAdvanced(localCached));
        if(valid) {
            //cachePort();
            return localCached;
        }
        tryCachePort();
        return 0;
    }

    private static int __freePort() {
        final int src = Wrapper.getInstance().getSetting().getStartPort();
        final int range = Wrapper.getInstance().getSetting().getPortRange();
        int port = ThreadLocalRandom.current().nextInt(range) + src;
        while(isPortUsed(port)) {
            port = ThreadLocalRandom.current().nextInt(range) + src;
        }
        return port;
    }

    public static void tryCachePort() {
        int current = cached.get();
        if(current == 0 || isPortUsed(current))
            cachePort();
    }

    private static void cachePort() {
        cached.set(__freePort());
    }

    private static boolean isPortUsed(int port) {
        try {
            new ServerSocket(port).close();
            return false;
        } catch (Exception ex) {
            return true;
        }
    }

    private static boolean checkUsageAdvanced(int port) {
        Wrapper instance = Wrapper.getInstance();
        if(instance != null && instance.getServerManager() != null && instance.getProxyManager() != null) {
            for(GroupInstance group : instance.getServerManager().getGroups()) {
                for(Object server : group.getServers()) {
                    ServerInstance casted = (ServerInstance) server;
                    if(casted.getPort() == port)
                        return true;
                }
            }
            for(GroupInstance group : instance.getProxyManager().getGroups()) {
                for(Object server : group.getServers()) {
                    ServerInstance casted = (ServerInstance) server;
                    if(casted.getPort() == port)
                        return true;
                }
            }
        }
        return isPortUsed(port);
    }
    */

    private static boolean isPortUsed(int port) {
        try {
            new ServerSocket(port).close();
            return false;
        } catch (Exception ex) {
            return true;
        }
    }

    private static boolean checkUsageAdvanced(int port) {
        Wrapper instance = Wrapper.getInstance();
        if(instance != null && instance.getServerManager() != null && instance.getProxyManager() != null) {
            for(GroupInstance<?> group : instance.getServerManager().getGroups().values()) {
                for(ServerInstance<?> server : group.getServers()) {
                    if(server.getPort() == port)
                        return true;
                }
            }
            for(GroupInstance<?> group : instance.getProxyManager().getGroups().values()) {
                for(ServerInstance<?> server : group.getServers()) {
                    if(server.getPort() == port)
                        return true;
                }
            }
        }
        return isPortUsed(port);
    }

    public static int freePort() {
        final int src = Wrapper.getInstance().getSetting().getStartPort();
        final int range = Wrapper.getInstance().getSetting().getPortRange();
        int port = ThreadLocalRandom.current().nextInt(range) + src;
        while(checkUsageAdvanced(port)) {
            port = ThreadLocalRandom.current().nextInt(range) + src;
        }
        return port;
    }

}
