package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.lib.TypeCheckUtil;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketExecute;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.utils.StringUtil;

public final class CommandExecute extends Command {

    public CommandExecute() {
        super("Execute", "Executes a command on a specific instance!", "[-p/-s] [Server] [Command]", "exe", "exc", "exec");
    }

    @Override
    public void execute(String[] args) {
        if(args.length < 3 || (!args[0].equalsIgnoreCase("-p") && !args[0].equalsIgnoreCase("-s") && !args[0].equalsIgnoreCase("-w"))) {
            Logger.log("Folgende Parameter werden benötigt: " + getUsage(), LogType.WARN);
            return;
        }
        GroupInstance<?> group;
        ServerInstance<?> server;
        final boolean proxy = args[0].equalsIgnoreCase("-p");
        final InstanceManager<?, ?> manager = proxy ? Master.getInstance().getProxyManager() : Master.getInstance().getServerManager();
        final String[] split = args[1].split("-", 2);
        group = manager.getGroup(split[0]);
        if(group == null) {
            Logger.log("Diese Gruppe existiert nicht!", LogType.WARN);
            return;
        }
        if(!TypeCheckUtil.isInteger(split[1])) {
            Logger.log("Dieser Server ist nicht online!", LogType.WARN);
            return;
        }
        final int serverId = Integer.parseInt(split[1]);
        server = group.getServer(serverId);
        if(server == null) {
            Logger.log("Dieser Server ist nicht online!", LogType.WARN);
            return;
        }
        server.getWrapper().sendPacket(new PacketExecute(split[0], serverId, proxy, (StringUtil.combine(args, 2, " ") + "\n").getBytes()));
        Logger.log("Der Befehl wurde erfolgreich ausgeführt!", LogType.INFO);
    }

}
