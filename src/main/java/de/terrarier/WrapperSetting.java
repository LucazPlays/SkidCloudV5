package de.terrarier;

import de.terrarier.lib.SqlSetting;
import de.terrarier.server.proxy.BungeeSetting;

public class WrapperSetting {

    private final boolean firstStarted;
    private final boolean saveServerLogs;
    private final String masterHost;
    private final int masterPort;
    private final int wrapperPort;
    private final int startPort;
    private final int portRange;
    private final SqlSetting sqlSetting;
    private final BungeeSetting bungeeSetting;

    public WrapperSetting(boolean firstStarted, boolean saveServerLogs, String masterHost, int masterPort, int wrapperPort, int startPort, int portRange, SqlSetting sqlSetting, BungeeSetting bungeeSetting) {
        this.firstStarted = firstStarted;
        this.saveServerLogs = saveServerLogs;
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        this.wrapperPort = wrapperPort;
        this.startPort = startPort;
        this.portRange = portRange;
        this.sqlSetting = sqlSetting;
        this.bungeeSetting = bungeeSetting;
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

    public SqlSetting getSqlSetting() {
        return this.sqlSetting;
    }

    public BungeeSetting getBungeeSetting() {
        return this.bungeeSetting;
    }

}
