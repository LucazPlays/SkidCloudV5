package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.lib.CustomPayloadUtil;
import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.multithreading.executables.proxy.StopProxy;
import de.terrarier.terracloud.multithreading.executables.spigot.StopServer;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.ServerInstance;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.proxy.ProxyServer;
import de.terrarier.terracloud.server.spigot.BukkitServer;
import de.terrarier.terracloud.server.spigot.ServerGroup;

public final class PacketCustomPayload extends Packet {

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {}

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        CustomPayloadUtil.PayloadBuilder payload = CustomPayloadUtil.getBuilder();
        payload.read(dataContainer);
        switch (payload.getId()) {
            case -1:
                ServerGroup group = Master.getInstance().getServerManager().getGroup(payload.getGroup()); // the group is null
                if(group == null) {
                    return;
                }
                BukkitServer server = group.getServer(payload.getServerId());
                if(server == null || !server.isStarted()) {
                    return;
                }
                server.getWrapper().sendData(dataContainer);
                break;
            case -2:
            case -7:
                Master.getInstance().broadcastData(dataContainer, Master.getInstance().getWrapperManager().fromConnection(connection));
                break;
            case -3:
                ServerGroup sGroup = Master.getInstance().getServerManager().getGroup((String) payload.getData()[0]);
                if(sGroup == null)
                    return;

                if (payload.doTargetSpecificServer() && sGroup.getServer((int) payload.getData()[1]) != null)
                    return;

                if (payload.doTargetSpecificServer()) {
                    if(payload.getData().length == 3) {
                        Master.getInstance().getServerManager().startServer(sGroup, (int) payload.getData()[1], ServerVersion.valueOf((String) payload.getData()[2]));
                    }else {
                        Master.getInstance().getServerManager().startServer(sGroup, (int) payload.getData()[1]);
                    }
                }else {
                    if(payload.getData().length == 2) {
                        // TODO: Fix this!
                        /*
                        ServerInstance<?> sourceServer = Master.getInstance().getServerManager().getServer(connection);
                        if(sourceServer == null) {
                            sourceServer = Master.getInstance().getProxyManager().getServer(connection);
                        }
                        Master.getInstance().getServerManager().startServer(sGroup,
                                new CallbackReference(sourceServer.getGroup() instanceof ProxyGroup, sourceServer.getGroup().getName(), sourceServer.getId(), (UUID) payload.getData()[1]));
                        */
                    }else {
                        Master.getInstance().getServerManager().startServer(sGroup);
                    }
                }
                break;
            case -4:
                ServerGroup kGroup = Master.getInstance().getServerManager().getGroup((String) payload.getData()[0]);
                if(kGroup == null)
                    return;

                BukkitServer kServer = kGroup.getServer((int) payload.getData()[1]);

                if(kServer == null) {
                    return;
                }

                Master.getInstance().getExecutorService().executeAsync(new StopServer(kServer));
                break;
            case -5:
                ProxyGroup spGroup = Master.getInstance().getProxyManager().getGroup((String) payload.getData()[0]);
                if(spGroup == null)
                    return;

                if (payload.doTargetSpecificServer() && spGroup.getServer((int) payload.getData()[1]) != null)
                    return;
                Master.getInstance().getProxyManager().startServer(spGroup);
                break;
            case -6:
                ProxyGroup kpGroup = Master.getInstance().getProxyManager().getGroup((String) payload.getData()[0]);
                if(kpGroup == null)
                    return;

                final ProxyServer kpServer = kpGroup.getServer((int) payload.getData()[1]);

                if(kpServer == null) {
                    return;
                }

                Master.getInstance().getExecutorService().executeAsync(new StopProxy(kpServer));
                break;
            default:
                if(payload.doHandleInternally()) {
                    return;
                }

                if(!payload.doTargetSpecificGroup()) {
                    Master.getInstance().broadcastData(dataContainer, Master.getInstance().getWrapperManager().fromConnection(connection));
                    return;
                }

                InstanceManager<?, ?> manager = payload.isProxyGroup() ? Master.getInstance().getProxyManager() : Master.getInstance().getServerManager();
                GroupInstance<?> fGroup = manager.getGroup(payload.getGroup());
                if(fGroup == null) {
                    return;
                }

                if(payload.doTargetSpecificServer()) {
                    ServerInstance<?> fServer = fGroup.getServer(payload.getServerId());

                    if(fServer == null) {
                        return;
                    }

                    fServer.getWrapper().sendData(dataContainer);
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
