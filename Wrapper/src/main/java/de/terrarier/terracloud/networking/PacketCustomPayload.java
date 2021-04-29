package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.lib.CustomPayloadUtil;
import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.multithreading.executables.proxy.StopProxy;
import de.terrarier.terracloud.multithreading.executables.spigot.StopServer;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.server.proxy.LocalProxyServerImpl;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.LocalBukkitServerImpl;
import de.terrarier.terracloud.server.spigot.ServerGroup;
import de.terrarier.terracloud.utils.CallbackReference;

import java.util.UUID;

public final class PacketCustomPayload extends Packet {

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {}

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        CustomPayloadUtil.PayloadBuilder payload = CustomPayloadUtil.getBuilder();
        payload.read(dataContainer);
        switch (payload.getId()) {
            case -1:
                ServerGroup group = Wrapper.getInstance().getServerManager().getGroup(payload.getGroup()); // the group is null
                if(group == null) {
                    return;
                }
                BukkitServer server = group.getServer(payload.getServerId());
                if(server == null || !server.isStarted()) {
                    return;
                }
                if(server instanceof LocalBukkitServerImpl) {
                    ((LocalBukkitServerImpl) server).warmUp();
                }else {
                    Wrapper.getInstance().sendToMaster(dataContainer);
                }
                break;
            case -2:
                for(ProxyGroup pGroup : Wrapper.getInstance().getProxyManager().getGroups().values()) {
                    for(ProxyServer pServer : pGroup.getServers()) {
                        if(pServer.isStarted() && pServer instanceof LocalProxyServerImpl) {
                            ((LocalProxyServerImpl) pServer).getConnection().sendData(dataContainer);
                        }
                    }
                }
                Wrapper.getInstance().sendToMaster(dataContainer);
                break;
            case -3:
                ServerGroup sGroup = Wrapper.getInstance().getServerManager().getGroup((String) payload.getData()[0]);
                if(sGroup == null)
                    return;

                if (payload.doTargetSpecificServer() && sGroup.getServer((int) payload.getData()[1]) != null)
                    return;

                if (payload.doTargetSpecificServer()) {
                    if(payload.getData().length == 3) {
                        Wrapper.getInstance().getServerManager().startServer(sGroup, (int) payload.getData()[1], ServerVersion.valueOf((String) payload.getData()[2]));
                    }else {
                        Wrapper.getInstance().getServerManager().startServer(sGroup, (int) payload.getData()[1]);
                    }
                }else {
                    if(payload.getData().length == 2) {
                        ServerInstance<?> sourceServer = Wrapper.getInstance().getServerManager().getServer(connection);
                        if(sourceServer == null) {
                            sourceServer = Wrapper.getInstance().getProxyManager().getServer(connection);
                        }
                        Wrapper.getInstance().getServerManager().startServer(sGroup,
                                new CallbackReference(sourceServer.getGroup() instanceof ProxyGroup, sourceServer.getGroup().getName(), sourceServer.getId(), (UUID) payload.getData()[1]));
                    }else {
                        Wrapper.getInstance().getServerManager().startServer(sGroup);
                    }
                }
                break;
            case -4:
                ServerGroup kGroup = Wrapper.getInstance().getServerManager().getGroup((String) payload.getData()[0]);
                if(kGroup == null)
                    return;

                BukkitServer kServer = kGroup.getServer((int) payload.getData()[1]);

                if(kServer == null) {
                    return;
                }

                Wrapper.getInstance().getExecutorService().executeAsync(new StopServer(kServer));
                break;
            case -5:
                ProxyGroup spGroup = Wrapper.getInstance().getProxyManager().getGroup((String) payload.getData()[0]);
                if(spGroup == null)
                    return;

                if (payload.doTargetSpecificServer() && spGroup.getServer((int) payload.getData()[1]) != null)
                    return;
                Wrapper.getInstance().getProxyManager().startServer(spGroup);
                break;
            case -6:
                ProxyGroup kpGroup = Wrapper.getInstance().getProxyManager().getGroup((String) payload.getData()[0]);
                if(kpGroup == null)
                    return;

                final ProxyServer kpServer = kpGroup.getServer((int) payload.getData()[1]);

                if(kpServer == null) {
                    return;
                }

                Wrapper.getInstance().getExecutorService().executeAsync(new StopProxy(kpServer));
                break;
            case -7:
                Wrapper.getInstance().broadcastData(dataContainer);
                Wrapper.getInstance().sendToMaster(dataContainer);
                break;
            default:
                if(payload.doHandleInternally()) {
                    return;
                }

                if(!payload.doTargetSpecificGroup()) {
                    Wrapper.getInstance().broadcastData(dataContainer);
                    Wrapper.getInstance().sendToMaster(dataContainer);
                    return;
                }

                InstanceManager<?, ?> manager = payload.isProxyGroup() ? Wrapper.getInstance().getProxyManager() : Wrapper.getInstance().getServerManager();
                GroupInstance<?> fGroup = manager.getGroup(payload.getGroup());
                if(fGroup == null)
                    return;

                if(payload.doTargetSpecificServer()) {
                    ServerInstance<?> fServer = fGroup.getServer(payload.getServerId());

                    if(fServer == null) {
                        return;
                    }

                    if(fServer instanceof LocalServerInstance<?>) {
                        ((LocalServerInstance<?>) fServer).getConnection().sendData(dataContainer);
                    }else {
                        Wrapper.getInstance().sendToMaster(dataContainer);
                    }
                    return;
                }

                fGroup.sendData(dataContainer);
                break;
        }
    }

    @Override
    public int getId() {
        return 0x11;
    }

}
