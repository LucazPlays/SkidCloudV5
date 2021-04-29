package de.terrarier.command.commands;

import de.terrarier.Wrapper;
import de.terrarier.lib.ServerVersion;
import de.terrarier.lib.TypeCheckUtil;
import de.terrarier.logging.Color;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.command.Command;
import de.terrarier.server.proxy.ProxyGroup;
import de.terrarier.server.spigot.ServerGroup;

public final class CommandCreateGroup extends Command {

    public CommandCreateGroup() {
        super("CreateGroup", "Erstellt eine neue Gruppe.", "[-p/-s] [Name] [Servers] [Memory] [Dynamic,Static] " + Color.ITALIC + Color.YELLOW + "[ServerVersion]");
    }

    @Override
    public void execute(String[] args) {
        if(args == null ||
                (args.length != 5 && (args.length != 6 || !args[0].equalsIgnoreCase("-s"))) ||
                (!args[0].equalsIgnoreCase("-p") && !args[0].equalsIgnoreCase("-s")) ||
                !TypeCheckUtil.isInteger(args[2]) ||
                !TypeCheckUtil.isInteger(args[3]) ||
                (!args[4].equalsIgnoreCase("Dynamic") && !args[4].equalsIgnoreCase("Static"))) {
           /* Logger.log("" + (args.length != 5 && (args.length != 6 || !args[0].equalsIgnoreCase("-s"))), LogType.INFO);
            Logger.log("" + (!args[0].equalsIgnoreCase("-p") && !args[0].equalsIgnoreCase("-s")), LogType.INFO);
            Logger.log("" + !TypeCheckUtil.isInteger(args[2]), LogType.INFO);
            Logger.log("" + !TypeCheckUtil.isInteger(args[3]), LogType.INFO);
            Logger.log("" + (!args[4].equalsIgnoreCase("Dynamic") && !args[4].equalsIgnoreCase("Static")), LogType.INFO);*/
            Logger.log("Folgende Parameter sind erforderlich \"" + getUsage() + Color.RESET + "\"!", LogType.WARN);
            return;
        }
        final boolean proxy = args[0].equalsIgnoreCase("-p");
        if(proxy) {
            if(Wrapper.getInstance().getProxyManager().getGroup(args[1]) != null) {
                Logger.log("Diese ProxyGruppe existiert bereits!", LogType.WARN);
                return;
            }
            Wrapper.getInstance().getProxyManager().createGroup(new ProxyGroup(args[1], Integer.parseInt(args[2]), args[4].equalsIgnoreCase("dynamic"), Integer.parseInt(args[3])));
            Logger.log("Die ProxyGruppe \"" + args[1] + "\" wurde erstellt!", LogType.INFO);
            return;
        }
        if(Wrapper.getInstance().getServerManager().getGroup(args[1]) != null) {
            Logger.log("Diese Gruppe existiert bereits!", LogType.WARN);
            return;
        }
        ServerVersion serverVersion;
        try {
            serverVersion = args.length == 6 ? ServerVersion.valueOf((args[5].startsWith("V") ? args[5] : "V" + args[5]).replace('.', '_')) : ServerVersion.V1_8_8;
        }catch (Exception e) {
            // Logger.log((args[5].startsWith("V") ? args[5] : "V" + args[5]).replace('.', '_'), LogType.INFO);
            Logger.log("Diese Serverversion wird nicht unterstützt!", LogType.WARN);
            return;
        }
        Wrapper.getInstance().getServerManager().createGroup(new ServerGroup(args[1], Integer.parseInt(args[2]), args[4].equalsIgnoreCase("dynamic"), Integer.parseInt(args[3]), serverVersion));
        Logger.log("Die Gruppe \"" + args[1] + "\" wurde erstellt!", LogType.INFO);
    }

}
