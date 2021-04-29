package de.terrarier.multithreading.executables.spigot;

import de.terrarier.file.FileManager;
import de.terrarier.Wrapper;
import de.terrarier.logging.LogType;
import de.terrarier.logging.Logger;
import de.terrarier.terracloud.packet.PacketUnRegister;
import de.terrarier.server.spigot.BukkitServer;
import de.terrarier.server.spigot.ServerGroup;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class StopServer implements Runnable {

    private final BukkitServer server;
    private final boolean preventRestart;

    public StopServer(BukkitServer server) {
        this(server, false);
    }

    public StopServer(BukkitServer server, boolean preventRestart) {
        this.server = server;
        this.preventRestart = preventRestart;
    }

    @Override
    public void run() {
        final ServerGroup group = this.server.getGroup();
        final int id = this.server.getId();
        group.removeServer(id);
        final String path = Wrapper.getInstance().getServerManager().getPath(group.isDynamic(), group.getName(), id);
        if(group.isDynamic()) {
            if(Wrapper.getInstance().getSetting().isSaveServerLogs()) {
                File logs = new File(path + "/logs/");
                File[] files = logs.listFiles();
                if(logs.exists() && logs.isDirectory() && files != null && files.length != 0) {
                    try {
                        FileManager.copy(files[0].getAbsolutePath(), "./Wrapper/ServerLogs/" + group.getName() + "-" + id + "-" + System.currentTimeMillis());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            FileManager.deleteDir(new File(path));
            /*
            if (!this.preventRestart && group.getServerCount() >= id) {
                Wrapper.getInstance().getExecutorService().executeDelayed(() -> new StartServer(group).run(), 1, TimeUnit.SECONDS);
            }
             */
        }else {
            File logDir = new File("./Wrapper/Servers/" + group.getName() + "/" + group.getName() + "-" + id + "/logs");
            if(logDir.exists() && logDir.isDirectory()) {
                File[] logs = logDir.listFiles();
                if(logs != null && logs.length > 0) {
                    for(File log : logs) {
                        if(log.lastModified() + 28800000L < System.currentTimeMillis()) {
                            log.delete();
                        }
                    }
                }
            }
        }
        if (!this.preventRestart && group.getServerCount() >= id) {
           // Wrapper.getInstance().getExecutorService().executeDelayed(() -> new StartServer(group).run(), 1, TimeUnit.SECONDS);
            Wrapper.getInstance().getExecutorService().executeDelayed(() -> new StartServer(group).run(), 250, TimeUnit.MILLISECONDS);
        }
        //ProcessManager.executeAndDestroyProcess(new ProcessBuilder(Arrays.asList("pkill", "-9", "-f", "s_" + group.getName() + "-" + id)));
        this.server.getProcess().destroyForcibly();
        Logger.log("Der Server " + group.getName() + "-" + this.server.getId() + " wurde gestoppt!", LogType.INFO);
        Wrapper.getInstance().broadcastPacket(new PacketUnRegister(this.server.getGroup().getName(), this.server.getId()));
    }

}
