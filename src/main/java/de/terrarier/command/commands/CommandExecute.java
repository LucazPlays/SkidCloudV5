package de.terrarier.command.commands;

import de.terrarier.Wrapper;
import de.terrarier.lib.TypeCheckUtil;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.command.Command;
import de.terrarier.server.GroupInstance;
import de.terrarier.server.InstanceManager;
import de.terrarier.server.ServerInstance;
import de.terrarier.utils.StringUtil;

import java.io.IOException;

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
        final InstanceManager manager = args[0].equalsIgnoreCase("-p") ? Wrapper.getInstance().getProxyManager() : Wrapper.getInstance().getServerManager();
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
        server = group.getServer(Integer.parseInt(split[1]));
        if(server == null || !server.getProcess().isAlive()) {
            Logger.log("Dieser Server ist nicht online!", LogType.WARN);
            return;
        }
        try {
            server.getProcess().getOutputStream().write((StringUtil.combine(args, 2, " ") + "\n").getBytes());
            server.getProcess().getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.log("Der Befehl wurde erfolgreich ausgeführt!", LogType.INFO);
    }

}
