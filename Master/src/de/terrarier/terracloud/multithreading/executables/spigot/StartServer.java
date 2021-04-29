package de.terrarier.terracloud.multithreading.executables.spigot;

import de.terrarier.terracloud.Master;
import de.terrarier.terracloud.lib.ServerVersion;
import de.terrarier.terracloud.logging.LogType;
import de.terrarier.terracloud.logging.Logger;
import de.terrarier.terracloud.networking.PacketStartInstance;
import de.terrarier.terracloud.server.spigot.ServerGroup;
import de.terrarier.terracloud.server.wrapper.Wrapper;
import de.terrarier.terracloud.utils.CallbackReference;

public final class StartServer implements Runnable {

    private final ServerGroup group;
    private final int id;
    private CallbackReference callbackReference;
    private ServerVersion serverVersion;

    public StartServer(ServerGroup group) {
        this(group, group.getNextId());
    }

    public StartServer(ServerGroup group, CallbackReference callbackReference) {
        this(group, group.getNextId());
        this.callbackReference = callbackReference;
    }

    public StartServer(ServerGroup group, int id) {
        this.group = group;
        this.id = id;
    }

    public StartServer(ServerGroup group, int id, ServerVersion serverVersion) {
        this(group, id);
        this.serverVersion = serverVersion;
    }

    @Override
    public void run() {
        if(group.isDeleted()) {
            return;
        }
        final Wrapper wrapper = Master.getInstance().getWrapperManager().getBestSuitableWrapper(group.getMemory());
        if (wrapper == null) {
            Master.getInstance().getWrapperManager().addTask(this, group.getMemory());
            return;
        }
        Logger.log("Der Server " + this.group.getName() + "-" + this.id + " wird nun gestartet...", LogType.INFO);
        group.addServer(this.id, wrapper);
        if (callbackReference != null) {
            callbackReference.triggerCallback(id);
        }
        wrapper.sendPacket(new PacketStartInstance(group.getName(), id, false));
    }

}
