package de.terrarier.terracloud.multithreading.executables.proxy;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketStartInstance;
import de.terrarier.terracloud.server.proxy.ProxyGroup;
import de.terrarier.terracloud.server.wrapper.Wrapper;

public final class StartProxy implements Runnable {

    private final ProxyGroup group;
    private final int id;

    public StartProxy(ProxyGroup group) {
        this(group, group.getNextId());
    }

    public StartProxy(ProxyGroup group, int id) {
        this.group = group;
        this.id = id;
    }

    @Override
    public void run() {
        if(group.isDeleted()) {
            return;
        }
        final Wrapper wrapper = Master.getInstance().getWrapperManager().getBestSuitableWrapper(group.getMemory());
        if(wrapper == null) {
            Master.getInstance().getWrapperManager().addTask(this, group.getMemory());
            return;
        }
        Logger.log("Der ProxyServer " + this.group.getName() + "-" + this.id + " wird nun gestartet...", LogType.INFO);
        group.addServer(id, wrapper);
        wrapper.sendPacket(new PacketStartInstance(group.getName(), id, true));
    }

}
