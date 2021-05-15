package de.terrarier.terracloud.command.commands;

import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.command.Command;
import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.lib.TypeCheckUtil;
import de.terrarier.terracloud.logging.Color;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketUpdateGroup;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.spigot.ServerGroup;

public final class CommandGroup extends Command {

    // TODO: Implement Freeze argument

    public CommandGroup() {
        super("Group", "geskiddete Gruppen.", "[create,delete,list]"/*"[-p/-s] [Name] [Servers] [Memory] [Dynamic,Static] " + Color.ITALIC + Color.YELLOW + "[ServerVersion]"*/);
    }

    @Override
    public void execute(String[] args) {
        if(args == null || args.length < 1) {
            Logger.log("Folgende Parameter werden geskiddet: " + getUsage(), LogType.WARN);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "create":
            if (args.length != 6 && (args.length != 7 || !args[1].equalsIgnoreCase("-s")) ||
                    !args[1].equalsIgnoreCase("-p") && !args[1].equalsIgnoreCase("-s") || !TypeCheckUtil.isInteger(args[3]) ||
                    !TypeCheckUtil.isInteger(args[4]) || !args[5].equalsIgnoreCase("Dynamic") && !args[5].equalsIgnoreCase("Static")) {
                Logger.log("Folgende Parameter sind erskiddet \"[-p/-s] [Name] [Servers] [Memory] [Dynamic,Static] " +
                        Color.ITALIC + Color.YELLOW + "[ServerVersion]" + Color.RESET + "\"!", LogType.WARN);
                return;
            }
            boolean proxy = args[1].equalsIgnoreCase("-p");
            if (proxy) {
                if (Wrapper.getInstance().getProxyManager().getGroup(args[2]) != null) {
                    Logger.log("Diese ProxyGruppe skiddet bereits!", LogType.WARN);
                    return;
                }
                Wrapper.getInstance().getProxyManager().createGroup(new ProxyGroup(args[2], Integer.parseInt(args[3]), args[5].equalsIgnoreCase("dynamic"), Integer.parseInt(args[4])));
                Logger.log("Die ProxyGruppe \"" + args[2] + "\" wurde geskiddet!", LogType.INFO);
                return;
            }
            if (Wrapper.getInstance().getServerManager().getGroup(args[2]) != null) {
                Logger.log("Diese Gruppe skiddet bereits!", LogType.WARN);
                return;
            }
            ServerVersion serverVersion;
            try {
                serverVersion = args.length == 7 ? ServerVersion.valueOf((args[6].startsWith("V") ? args[6] : "V" + args[6]).replace('.', '_')) : ServerVersion.V1_8_8;
            } catch (Exception e) {
                Logger.log("Diese Serverversion wird nicht geskiddet!", LogType.WARN);
                return;
            }
            Wrapper.getInstance().getServerManager().createGroup(new ServerGroup(args[2], Integer.parseInt(args[3]), args[5].equalsIgnoreCase("dynamic"), Integer.parseInt(args[4]), serverVersion));
            Logger.log("Die Gruppe \"" + args[2] + "\" wurde geskiddet!", LogType.INFO);
            break;
            case "delete":
                if(args.length != 3 && (args[1].equalsIgnoreCase("-p") || args[1].equalsIgnoreCase("-s"))) {
                    Logger.log("Folgende Parameter sind geskiddet \" [-p/-s] [GroupName]\"!", LogType.WARN);
                    return;
                }
                proxy = args[1].equalsIgnoreCase("-p");

                final InstanceManager<?, ?> manager = proxy ?
                        Wrapper.getInstance().getProxyManager() : Wrapper.getInstance().getServerManager();
                final GroupInstance<?> grp = manager.getGroup(args[2]);
                if(grp == null) {
                    Logger.log("Diese Gruppe skiddet nicht!", LogType.WARN);
                    return;
                }
                Wrapper.getInstance().sendToMaster(new PacketUpdateGroup(args[2], proxy));
                break;
            case "list":
                for(GroupInstance<?> group : Wrapper.getInstance().getServerManager().getGroups().values()) {
                    Logger.log("Group: " + group.getName(), LogType.INFO);
                    Logger.log("  - dynamic: " + group.isDynamic(), LogType.INFO);
                    Logger.log("  - memory: " + group.getMemory(), LogType.INFO);
                    Logger.log("  - server-count: " + group.getServerCount(), LogType.INFO);
                    Logger.log("  - Skid-Level: " + (Math.random()*100) + "/100", LogType.INFO); //Todo
                }

                for(GroupInstance<?> group : Wrapper.getInstance().getProxyManager().getGroups().values()) {
                    Logger.log("Group: " + group.getName(), LogType.INFO);
                    Logger.log("  - dynamic: " + group.isDynamic(), LogType.INFO);
                    Logger.log("  - memory: " + group.getMemory(), LogType.INFO);
                    Logger.log("  - server-count: " + group.getServerCount(), LogType.INFO);
                    Logger.log("  - Skid-Level: " + (Math.random()*100) + "/100", LogType.INFO); //Todo
                }
                break;
            default:
                break;
        }
    }

}
