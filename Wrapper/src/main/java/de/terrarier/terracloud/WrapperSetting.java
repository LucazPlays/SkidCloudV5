package de.terrarier.terracloud;

import de.terrarier.terracloud.server.proxy.BungeeSetting;

import java.util.UUID;

public class WrapperSetting {

    private final String name;
    private final boolean firstStarted;
    private final boolean saveServerLogs;
    private final String masterHost;
    private final int masterPort;
    private final int wrapperPort;
    private final int startPort;
    private final int portRange;
    private final UUID key;
    private BungeeSetting bungeeSetting;

    public WrapperSetting(String name, boolean firstStarted, boolean saveServerLogs, String masterHost, int masterPort, int wrapperPort,
                          int startPort, int portRange, UUID key) {
        this.name = name;
        this.firstStarted = firstStarted;
        this.saveServerLogs = saveServerLogs;
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        this.wrapperPort = wrapperPort;
        this.startPort = startPort;
        this.portRange = portRange;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public boolean isFirstStarted() {
        return this.firstStarted;
    }

    public boolean isSaveServerLogs() {
        return this.saveServerLogs;
    }

    public int getMasterPort() {
        return this.masterPort;
    }

    public String getMasterHost() {
        return this.masterHost;
    }

    public int getWrapperPort() {
        return this.wrapperPort;
    }

    public int getStartPort() {
        return this.startPort;
    }

    public int getPortRange() {
        return this.portRange;
    }

    public UUID getKey() {
        return key;
    }

    public BungeeSetting getBungeeSetting() {
        return bungeeSetting;
    }

    public void setBungeeSetting(BungeeSetting bungeeSetting) {
        this.bungeeSetting = bungeeSetting;
    }

}
