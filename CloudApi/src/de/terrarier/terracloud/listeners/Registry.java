package de.terrarier.terracloud.listeners;

import de.terrarier.terracloud.CloudApi;
import de.terrarier.terracloud.networking.PacketPlayerCountUpdate;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class Registry implements Listener {

    private int playerCount;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        final String host = event.getRealAddress().getHostAddress();
        if(host.equals("127.0.0.1")) {
            return;
        }
        for(ProxyGroup group : CloudApi.getInstance().getProxyManager().getGroups().values()) {
            for(ProxyServer server : group.getServers()) {
                if(server.getHost().equals(host)) {
                    return;
                }
            }
        }
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cDu musst von einem anerkannten Proxy-Server beitreten!");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent ev) {
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all.getName().equalsIgnoreCase(ev.getName())) {
                ev.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "§cDu befindest dich bereits auf dem Server!");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(PlayerJoinEvent ev) {
        CloudApi.getInstance().sendPacket(new PacketPlayerCountUpdate(++playerCount));
    }

    @EventHandler
    public void onKick(PlayerKickEvent ev) {
        if(ev.getReason().equals("Logged in from another location.") || ev.getReason().equals("You logged in from another location")) {
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent ev) {
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(ev.getPlayer() != all && ev.getPlayer().getName().equalsIgnoreCase(all.getName())) {
                ev.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cEin Spieler mit deinem Namen befindet sich bereits auf dem Server!");
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDisconnect(PlayerQuitEvent ev) {
        CloudApi.getInstance().sendPacket(new PacketPlayerCountUpdate(--playerCount));
    }

}
