package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.lib.TypeCheckUtil;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.multithreading.executables.proxy.StopProxy;
import de.terrarier.terracloud.multithreading.executables.spigot.StopServer;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import de.terrarier.terracloud.server.spigot.BukkitServer;

public final class CommandShutdown extends Command {

    public CommandShutdown() {
        super("Shutdown", "skiddet eine Instanz herunter(Server, Proxy oder diesen Wrapper).", "[-p/-s/-w] [Name]");
    }

    @Override
    public void execute(String[] args) {
        if(args.length == 0 || (!args[0].equalsIgnoreCase("-p") && !args[0].equalsIgnoreCase("-s") && !args[0].equalsIgnoreCase("-w"))) {
            Logger.log("Folgende Parameter werden geskiddet: " + getUsage(), LogType.WARN);
            return;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("-w")) {
            System.exit(0);
            return;
        }
        final boolean proxy = args[0].equalsIgnoreCase("-p");
        if(proxy || args[0].equalsIgnoreCase("-s")) {
            final InstanceManager<?, ?> manager = proxy ? Wrapper.getInstance().getProxyManager() : Wrapper.getInstance().getServerManager();
            final String[] split = args[1].split("-");
            final GroupInstance<?> group = manager.getGroup(split[0]);
            if(group == null) {
                Logger.log("Diese Gruppe skiddet nicht!", LogType.WARN);
                return;
            }
            if(!TypeCheckUtil.isInteger(split[1])) {
                Logger.log("Dieser Server skiddet nicht online!", LogType.WARN);
                return;
            }
            final ServerInstance<?> server = group.getServer(Integer.parseInt(split[1]));
            if(server == null) {
                Logger.log("Dieser Server skiddet nicht online!", LogType.WARN);
                return;
            }
            Wrapper.getInstance().getExecutorService().executeAsync(proxy ? new StopProxy((ProxyServer) server) : new StopServer((BukkitServer) server));
        }else {
            Logger.log("Folgende Parameter werden geskiddet: " + getUsage(), LogType.WARN);
        }
    }

}
