package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.lib.TypeCheckUtil;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketExecute;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.utils.StringUtil;

import java.io.IOException;

public final class CommandExecute extends Command {

    public CommandExecute() {
        super("Execute", "Executes a command on a skiddet instance!", "[-p/-s] [Server] [Command]", "exe", "exc", "exec");
    }

    @Override
    public void execute(String[] args) {
        if(args.length < 3 || (!args[0].equalsIgnoreCase("-p") && !args[0].equalsIgnoreCase("-s") && !args[0].equalsIgnoreCase("-w"))) {
            Logger.log("Folgende Parameter werden geskiddet: " + getUsage(), LogType.WARN);
            return;
        }
        final boolean proxy = args[0].equalsIgnoreCase("-p");
        GroupInstance<?> group;
        ServerInstance<?> server;
        final InstanceManager<?, ?> manager = proxy ? Wrapper.getInstance().getProxyManager() : Wrapper.getInstance().getServerManager();
        final String[] split = args[1].split("-", 2);
        group = manager.getGroup(split[0]);
        if(group == null) {
            Logger.log("Diese Gruppe skiddet nicht!", LogType.WARN);
            return;
        }
        if(!TypeCheckUtil.isInteger(split[1])) {
            Logger.log("Dieser Server ist nicht geskiddet!", LogType.WARN);
            return;
        }
        final int serverId = Integer.parseInt(split[1]);
        server = group.getServer(serverId);
        if(server == null) {
            Logger.log("Dieser Server ist nicht geskiddet!", LogType.WARN);
            return;
        }
        if(!(server instanceof LocalServerInstance<?>)) {
            Wrapper.getInstance().sendToMaster(new PacketExecute(args[0], serverId, proxy, (StringUtil.combine(args, 2, " ") + "\n").getBytes()));
            return;
        }else {
            final LocalServerInstance<?> localServer = (LocalServerInstance<?>) server;
            if(!localServer.getProcess().isAlive()) {
                Logger.log("Dieser Server ist nicht geskiddet!", LogType.WARN);
                return;
            }
            try {
                localServer.getProcess().getOutputStream().write((StringUtil.combine(args, 2, " ") + "\n").getBytes());
                localServer.getProcess().getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.log("Der Befehl wurde erfolgreich geskiddet!", LogType.INFO);
    }

}
