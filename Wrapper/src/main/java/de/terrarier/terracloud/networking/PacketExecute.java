package de.terrarier.terracloud.networking;

import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.terracloud.Wrapper;
import de.terrarier.terracloud.server.GroupInstance;
import de.terrarier.terracloud.server.InstanceManager;
import de.terrarier.terracloud.server.LocalServerInstance;
import de.terrarier.terracloud.server.ServerInstance;

import java.io.IOException;
import java.io.OutputStream;

public final class PacketExecute extends Packet {

    private String targetGroup;
    private int targetId;
    private boolean proxy;
    private byte[] command;

    public PacketExecute() {}

    public PacketExecute(String targetGroup, int targetId, boolean proxy, byte[] command) {
        this.targetGroup = targetGroup;
        this.targetId = targetId;
        this.proxy = proxy;
        this.command = command;
    }

    @Override
    protected void write0(DataContainer dataContainer, ServiceType direction) {
        dataContainer.addAll(targetGroup, targetId, proxy, command);
    }

    @Override
    protected void read0(DataContainer dataContainer, Connection connection) {
        targetGroup = dataContainer.read();
        targetId = dataContainer.read();
        proxy = dataContainer.read();
        command = dataContainer.read();
        final InstanceManager<?, ?> manager = proxy ? Wrapper.getInstance().getProxyManager() :
                Wrapper.getInstance().getServerManager();
        final GroupInstance<?> group = manager.getGroup(targetGroup);
        if(group != null) {
            final ServerInstance<?> server = group.getServer(targetId);
            if(server instanceof LocalServerInstance<?>) {
                final OutputStream outputStream = ((LocalServerInstance<?>) server).getProcess().getOutputStream();
                try {
                    outputStream.write(command);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Wrapper.getInstance().sendToMaster(this);
            }
        }
    }

    @Override
    public int getId() {
        return 0x7;
    }

}
