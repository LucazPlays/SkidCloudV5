package de.terrarier.terracloud.utils;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.ServerInstance;

import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;

public class PortUtil {

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
                    if(server instanceof LocalServerInstance && ((LocalServerInstance<?>) server).getPort() == port)
                        return true;
                }
            }
            for(GroupInstance<?> group : instance.getProxyManager().getGroups().values()) {
                for(ServerInstance<?> server : group.getServers()) {
                    if(server instanceof  LocalServerInstance && ((LocalServerInstance<?>) server).getPort() == port)
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
