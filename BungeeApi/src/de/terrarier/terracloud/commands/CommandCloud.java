package de.terrarier.terracloud.commands;

import de.terrarier.terracloud.BungeeCloudApi;
import de.terrarier.terracloud.lib.CustomPayloadUtil;
import de.terrarier.terracloud.lib.FileUtil;
import de.terrarier.terracloud.lib.TypeCheckUtil;
import de.terrarier.terracloud.networking.PacketExecute;
import de.terrarier.terracloud.networking.PacketReload;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.utils.StringUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;
import java.io.IOException;

public final class CommandCloud extends Command {

    public CommandCloud() {
        super("cloud");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "Nur Spieler dürfen diesen Befehl ausführen.");
            return;
        }
        final ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if(!player.hasPermission("terracloud.command")) {
            player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + BungeeCloudApi.getInstance().getMessages().getNoPerm());
            return;
        }
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "reload":
                        BungeeCloudApi.getInstance().sendPacket(new PacketReload());
                        player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "Versuche die Cloud zu reloaden...");
                        break;
                    case "copy":
                        final String serverName = player.getServer().getInfo().getName();
                        final String[] split = serverName.split("-");
                        final ServerInstance<?> server = BungeeCloudApi.getInstance().getServerManager().
                                getServer(split[0], Integer.parseInt(split[1]));
                        try {
                            FileUtil.copy(new File(BungeeCloudApi.WRAPPER_PATH +
                                    (server.getGroup().isDynamic() ? "Temp/" : "") + "Servers/" + split[0] + "/" +
                                    serverName), new File(BungeeCloudApi.WRAPPER_PATH + "Templates/Server/" + split[0]));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "Der Server wurde kopiert.");
                        break;
                    default:
                        sendHelp(player);
                        break;
                }
                break;
            case 3:
                switch (args[0].toLowerCase()) {
                    case "start":
                        switch (args[1].toLowerCase()) {
                            case "-s":
                                CustomPayloadUtil.startOrCreateServer(args[2]);
                                player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "§cVersuche den Server " + args[2] + " zu starten...");
                                break;
                            case "-p":
                                CustomPayloadUtil.startProxy(args[2]);
                                player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "§cVersuche den Server " + args[2] + " zu starten...");
                                break;
                            default:
                                sendHelp(player);
                                break;
                        }
                        break;
                    case "stop":
                        switch (args[1].toLowerCase()) {
                            case "-s":
                                String[] split = args[2].split("-", 2);
                                if(split.length == 0 || !TypeCheckUtil.isInteger(split[1])) {
                                    player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "§cBitte gib eine gültige Server id an!");
                                    return;
                                }
                                CustomPayloadUtil.stopServer(split[0], Integer.parseInt(split[1]));
                                player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "Versuche den Server §a" + args[2] + " §7zu stoppen...");
                                break;
                            case "-p":
                                split = args[2].split("-", 2);
                                if(!TypeCheckUtil.isInteger(split[1])) {
                                    player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "§cBitte gib eine gültige Server id an!");
                                    return;
                                }
                                CustomPayloadUtil.stopProxy(split[0], Integer.parseInt(split[1]));
                                player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "Versuche den Server §a" + args[2] + " §7zu stoppen...");
                                break;
                            default:
                                sendHelp(player);
                                break;
                        }
                        break;
                    default:
                        sendHelp(player);
                        break;
                }
                break;
            default:
                if(args.length >= 4 && args[0].equalsIgnoreCase("execute")) {
                    final String[] split = args[2].split("-");
                    if(split.length == 0 || !TypeCheckUtil.isInteger(split[1])) {
                        sendHelp(player);
                        return;
                    }
                    BungeeCloudApi.getInstance().sendPacket(new PacketExecute(split[0], Integer.parseInt(split[1]),
                            split[1].equalsIgnoreCase("-p"), (StringUtil.combine(args, 3, " ") + "\n").getBytes()));
                    player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "Versuche den Befehl §a" + args[2] + " §7auszuführen...");
                }else {
                    sendHelp(player);
                }
                break;
        }
    }

    private void sendHelp(ProxiedPlayer player) {
        player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "start [-p/-s] [Gruppe]");
        player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "stop [-p/-s] [Server]");
        player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "execute [-p/-s] [Server] {Command}");
        player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "reload");
        player.sendMessage(BungeeCloudApi.getInstance().getMessages().getPrefix() + "copy");
    }

}
