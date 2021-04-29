package de.terrarier.terracloud.listeners;

import de.terrarier.terracloud.BungeeCloudApi;
import de.terrarier.terracloud.server.proxy.BungeeSetting;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class Registry implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(PostLoginEvent ev) {
        BungeeCloudApi.getInstance().playerJoined();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(PreLoginEvent ev) {
        if (ev.isCancelled()) {
            return;
        }
        if (BungeeCord.getInstance().getServers().size() == 0) {
            ev.setCancelReason(BungeeCloudApi.getInstance().getMessages().getNoServer());
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onKick(ServerKickEvent ev) {
        if (ev.isCancelled()) {
            return;
        }
        if(ev.getKickReason().equals("§cYou are already connected to this proxy!")) {
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onKickCheck(PreLoginEvent ev) {
        if (ev.isCancelled()) {
            return;
        }
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if(player.getName().equals(ev.getConnection().getName()) &&
                    !player.getAddress().getAddress().getHostName().equals(
                            ev.getConnection().getAddress().getAddress().getHostName())) {
                ev.setCancelReason(BungeeCloudApi.getInstance().getMessages().getAlreadyConnected());
                ev.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(priority = 64)
    public void onPing(ProxyPingEvent ev) {
        final BungeeSetting setting = BungeeCloudApi.getInstance().getSetting();
        final ServerPing ping = ev.getResponse();
        ping.setDescription(ChatColor.translateAlternateColorCodes('&',
                (setting != null ? setting.getMotd() : "§cLoading...") +
                        (setting != null ? ("\n" + setting.getMotd2()) : "")));
        ping.setPlayers(new ServerPing.Players((setting != null ? setting.getSlots() : 100),
                ping.getPlayers().getOnline(), ping.getPlayers().getSample()));
        if (setting != null && setting.existsFavicon()) {
            ping.setFavicon(setting.getFavicon());
        }
        ev.setResponse(ping);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDisconnect(PlayerDisconnectEvent ev) {
        BungeeCloudApi.getInstance().playerQuit();
    }

}
